const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const app = express();
app.use(express.json());
app.use(cors());

app.get('/', (req, res) => {
  res.json({
    message: 'QuizApp backend is running',
    api: {
      questions: '/api/questions',
      register: '/api/auth/register',
      login: '/api/auth/login',
      saveScore: '/api/scores',
      latestScore: '/api/scores/latest'
    }
  });
});

// --- Database Connection ---
mongoose.set('bufferCommands', false);

mongoose.connect(process.env.MONGODB_URI, {
  serverSelectionTimeoutMS: 5000
})
  .then(() => console.log('Connected to MongoDB'))
  .catch(err => console.error('Could not connect to MongoDB', err));

const requireDatabase = (req, res, next) => {
  if (mongoose.connection.readyState !== 1) {
    return res.status(503).json({
      error: 'MongoDB is not connected. Start mongod, then restart the backend.'
    });
  }
  next();
};

// --- Models ---
const UserSchema = new mongoose.Schema({
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  name: { type: String, required: true },
  school: { type: String }
});
const User = mongoose.model('User', UserSchema);

const ScoreSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  score_iir: Number,
  score_gesi: Number,
  score_iaii: Number,
  score_gc: Number,
  score_gi: Number,
  score_gf: Number,
  createdAt: { type: Date, default: Date.now }
});
const Score = mongoose.model('Score', ScoreSchema);

const QuestionSchema = new mongoose.Schema({
  question_text: String,
  order_index: Number,
  options: [{
    option_text: String,
    target_major_code: String
  }]
});
const Question = mongoose.model('Question', QuestionSchema);

// --- Auth Middleware ---
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.sendStatus(401);

  jwt.verify(token, process.env.JWT_SECRET, (err, user) => {
    if (err) return res.sendStatus(403);
    req.user = user;
    next();
  });
};

// --- Routes ---

// Register
app.post('/api/auth/register', requireDatabase, async (req, res) => {
  try {
    const { password, name, school } = req.body;
    const email = String(req.body.email || '').trim().toLowerCase();
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = new User({ email, password: hashedPassword, name, school });
    await user.save();
    res.status(201).json({ message: 'User registered successfully' });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Login
app.post('/api/auth/login', requireDatabase, async (req, res) => {
  try {
    const { password } = req.body;
    const rawEmail = String(req.body.email || '').trim();
    const email = rawEmail.toLowerCase();
    const user = await User.findOne({ $or: [{ email }, { email: rawEmail }] });
    if (!user || !await bcrypt.compare(password, user.password)) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }
    const token = jwt.sign({ userId: user._id, email: user.email }, process.env.JWT_SECRET);
    res.json({ token, user: { id: user._id, email: user.email, name: user.name } });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get Questions
app.get('/api/questions', requireDatabase, async (req, res) => {
  try {
    const questions = await Question.find().sort('order_index');
    res.json(questions);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Save Score
app.post('/api/scores', requireDatabase, authenticateToken, async (req, res) => {
  try {
    const score = new Score({
      userId: req.user.userId,
      ...req.body
    });
    await score.save();
    res.status(201).json(score);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Get Latest Score
app.get('/api/scores/latest', requireDatabase, authenticateToken, async (req, res) => {
  try {
    const latestScore = await Score.findOne({ userId: req.user.userId }).sort('-createdAt');
    res.json(latestScore);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on http://0.0.0.0:${PORT}`);
  console.log(`Android phone URL: http://<PC_IPV4>:${PORT}`);
});
