const mongoose = require('mongoose');
require('dotenv').config();

const QuestionSchema = new mongoose.Schema({
  question_text: String,
  order_index: Number,
  options: [{
    option_text: String,
    target_major_code: String
  }]
});
const Question = mongoose.model('Question', QuestionSchema);

const sampleQuestions = [
  {
    question_text: "Qu'est-ce qui vous passionne le plus ?",
    order_index: 1,
    options: [
      { option_text: "Développement d'applications et réseaux", target_major_code: "IIR" },
      { option_text: "Systèmes embarqués et robotique", target_major_code: "GESI" },
      { option_text: "Intelligence artificielle et data science", target_major_code: "IAII" },
      { option_text: "Conception de bâtiments et infrastructures", target_major_code: "GC" },
      { option_text: "Optimisation de la production industrielle", target_major_code: "GI" },
      { option_text: "Analyse financière et marchés", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Dans quel type d'environnement aimeriez-vous travailler ?",
    order_index: 2,
    options: [
      { option_text: "Une entreprise technologique (GAFA, Startup)", target_major_code: "IIR" },
      { option_text: "Un laboratoire d'automatisme ou usine high-tech", target_major_code: "GESI" },
      { option_text: "Un centre de recherche en IA ou Big Data", target_major_code: "IAII" },
      { option_text: "Un chantier ou cabinet d'architecte", target_major_code: "GC" },
      { option_text: "Une grande usine ou chaine de logistique", target_major_code: "GI" },
      { option_text: "Une banque ou salle de marché", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel outil préférez-vous utiliser ?",
    order_index: 3,
    options: [
      { option_text: "Un éditeur de code (VS Code, IntelliJ)", target_major_code: "IIR" },
      { option_text: "Un oscilloscope ou fer à souder", target_major_code: "GESI" },
      { option_text: "Python et frameworks de Machine Learning", target_major_code: "IAII" },
      { option_text: "AutoCAD ou outils de calcul de structure", target_major_code: "GC" },
      { option_text: "Logiciels ERP ou gestion de stock", target_major_code: "GI" },
      { option_text: "Excel avancé et terminaux Bloomberg", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel type de problèmes aimez-vous résoudre ?",
    order_index: 4,
    options: [
      { option_text: "Bugs logiciels et architecture réseau", target_major_code: "IIR" },
      { option_text: "Pannes électroniques et automatisation", target_major_code: "GESI" },
      { option_text: "Optimisation d'algorithmes et analyse de données", target_major_code: "IAII" },
      { option_text: "Problèmes de structure et matériaux", target_major_code: "GC" },
      { option_text: "Goulots d'étranglement dans la production", target_major_code: "GI" },
      { option_text: "Évaluation de risques financiers", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel est votre sujet de veille technologique préféré ?",
    order_index: 5,
    options: [
      { option_text: "Cybersécurité et nouveaux frameworks Web", target_major_code: "IIR" },
      { option_text: "IoT et microcontrôleurs", target_major_code: "GESI" },
      { option_text: "Deep Learning et NLP", target_major_code: "IAII" },
      { option_text: "Smart Cities et matériaux écologiques", target_major_code: "GC" },
      { option_text: "Industrie 4.0 et Lean Management", target_major_code: "GI" },
      { option_text: "Fintech et Blockchain financière", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Comment préférez-vous passer votre temps libre ?",
    order_index: 6,
    options: [
      { option_text: "Contribuer à des projets Open Source", target_major_code: "IIR" },
      { option_text: "Bricoler des gadgets électroniques", target_major_code: "GESI" },
      { option_text: "Participer à des compétitions Kaggle", target_major_code: "IAII" },
      { option_text: "Visiter des monuments architecturaux", target_major_code: "GC" },
      { option_text: "Organiser et optimiser des processus", target_major_code: "GI" },
      { option_text: "Suivre l'actualité économique et boursière", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel est le projet de vos rêves ?",
    order_index: 7,
    options: [
      { option_text: "Créer un nouveau système d'exploitation", target_major_code: "IIR" },
      { option_text: "Concevoir un robot autonome", target_major_code: "GESI" },
      { option_text: "Développer une IA qui diagnostique des maladies", target_major_code: "IAII" },
      { option_text: "Bâtir un gratte-ciel innovant", target_major_code: "GC" },
      { option_text: "Révolutionner la logistique mondiale", target_major_code: "GI" },
      { option_text: "Gérer un fonds d'investissement majeur", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quelle compétence aimeriez-vous approfondir ?",
    order_index: 8,
    options: [
      { option_text: "Administration de serveurs Cloud", target_major_code: "IIR" },
      { option_text: "Conception de circuits intégrés", target_major_code: "GESI" },
      { option_text: "Traitement statistique des données", target_major_code: "IAII" },
      { option_text: "Géotechnique et résistance des matériaux", target_major_code: "GC" },
      { option_text: "Supply Chain Management", target_major_code: "GI" },
      { option_text: "Modélisation mathématique financière", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel aspect du travail en équipe préférez-vous ?",
    order_index: 9,
    options: [
      { option_text: "Code Review et Pair Programming", target_major_code: "IIR" },
      { option_text: "Intégration matériel-logiciel", target_major_code: "GESI" },
      { option_text: "Brainstorming sur des modèles prédictifs", target_major_code: "IAII" },
      { option_text: "Coordination sur un chantier de construction", target_major_code: "GC" },
      { option_text: "Optimisation de flux de travail", target_major_code: "GI" },
      { option_text: "Analyse stratégique de rentabilité", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel type de lecture vous attire le plus ?",
    order_index: 10,
    options: [
      { option_text: "Documentation technique et blogs tech", target_major_code: "IIR" },
      { option_text: "Revues sur l'électronique et la robotique", target_major_code: "GESI" },
      { option_text: "Articles scientifiques sur l'IA", target_major_code: "IAII" },
      { option_text: "Revues d'architecture et de génie civil", target_major_code: "GC" },
      { option_text: "Livres sur le management et l'industrie", target_major_code: "GI" },
      { option_text: "Journaux financiers (Les Échos, Wall Street Journal)", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quelle technologie vous semble la plus révolutionnaire ?",
    order_index: 11,
    options: [
      { option_text: "L'Internet des Objets (IoT)", target_major_code: "IIR" },
      { option_text: "L'impression 3D industrielle", target_major_code: "GESI" },
      { option_text: "L'Apprentissage par Renforcement", target_major_code: "IAII" },
      { option_text: "Les matériaux de construction intelligents", target_major_code: "GC" },
      { option_text: "La robotique collaborative (Cobotique)", target_major_code: "GI" },
      { option_text: "Les algorithmes de trading haute fréquence", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Dans une équipe de projet, vous seriez plutôt...",
    order_index: 12,
    options: [
      { option_text: "Le développeur Full Stack", target_major_code: "IIR" },
      { option_text: "L'ingénieur système embarqué", target_major_code: "GESI" },
      { option_text: "Le Data Scientist", target_major_code: "IAII" },
      { option_text: "Le chef de projet BTP", target_major_code: "GC" },
      { option_text: "Le responsable Qualité/Process", target_major_code: "GI" },
      { option_text: "L'analyste financier", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel type de données préférez-vous manipuler ?",
    order_index: 13,
    options: [
      { option_text: "Requêtes API et JSON", target_major_code: "IIR" },
      { option_text: "Signaux électriques et capteurs", target_major_code: "GESI" },
      { option_text: "Datasets massifs et non structurés", target_major_code: "IAII" },
      { option_text: "Plans 2D/3D et mesures topographiques", target_major_code: "GC" },
      { option_text: "Indicateurs de performance (KPI) industriels", target_major_code: "GI" },
      { option_text: "Séries temporelles boursières", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel est votre environnement de test idéal ?",
    order_index: 14,
    options: [
      { option_text: "Un environnement Docker/Kubernetes", target_major_code: "IIR" },
      { option_text: "Un banc d'essai électronique", target_major_code: "GESI" },
      { option_text: "Un notebook Jupyter avec GPU", target_major_code: "IAII" },
      { option_text: "Un logiciel de simulation de charge", target_major_code: "GC" },
      { option_text: "Une ligne de production pilote", target_major_code: "GI" },
      { option_text: "Une plateforme de Backtesting financier", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel défi technique vous motive ?",
    order_index: 15,
    options: [
      { option_text: "Sécuriser une infrastructure réseau", target_major_code: "IIR" },
      { option_text: "Optimiser la consommation d'énergie d'un circuit", target_major_code: "GESI" },
      { option_text: "Réduire le biais d'un modèle d'IA", target_major_code: "IAII" },
      { option_text: "Concevoir des structures résistantes aux séismes", target_major_code: "GC" },
      { option_text: "Réduire les coûts de production sans perte de qualité", target_major_code: "GI" },
      { option_text: "Prédire les tendances du marché", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quelle est votre approche face à un problème ?",
    order_index: 16,
    options: [
      { option_text: "Décomposer en petits modules de code", target_major_code: "IIR" },
      { option_text: "Vérifier les composants physiques", target_major_code: "GESI" },
      { option_text: "Analyser les corrélations statistiques", target_major_code: "IAII" },
      { option_text: "Faire un schéma structurel", target_major_code: "GC" },
      { option_text: "Analyser le flux du processus", target_major_code: "GI" },
      { option_text: "Évaluer le rapport coût/bénéfice", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel type de logiciel aimeriez-vous maîtriser ?",
    order_index: 17,
    options: [
      { option_text: "Gestionnaires de bases de données (PostgreSQL, NoSQL)", target_major_code: "IIR" },
      { option_text: "Logiciels de CAO électronique (Eagle, Altium)", target_major_code: "GESI" },
      { option_text: "Frameworks Deep Learning (PyTorch, TensorFlow)", target_major_code: "IAII" },
      { option_text: "Logiciels de calcul de structures (Robot, Revit)", target_major_code: "GC" },
      { option_text: "Systèmes de gestion de production (SAP, ERP)", target_major_code: "GI" },
      { option_text: "Logiciels d'analyse financière quantitative", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel secteur industriel vous intéresse le plus ?",
    order_index: 18,
    options: [
      { option_text: "Services numériques et Cloud", target_major_code: "IIR" },
      { option_text: "Automobile et Aéronautique", target_major_code: "GESI" },
      { option_text: "Santé et Recherche Tech", target_major_code: "IAII" },
      { option_text: "Bâtiment et Travaux Publics", target_major_code: "GC" },
      { option_text: "Industrie Manufacturière", target_major_code: "GI" },
      { option_text: "Secteur Bancaire et Assurances", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quelle valeur privilégiez-vous dans un projet ?",
    order_index: 19,
    options: [
      { option_text: "Scalabilité et Robustesse du code", target_major_code: "IIR" },
      { option_text: "Précision et Fiabilité du matériel", target_major_code: "GESI" },
      { option_text: "Performance des prédictions", target_major_code: "IAII" },
      { option_text: "Sécurité et Durabilité des ouvrages", target_major_code: "GC" },
      { option_text: "Efficacité et Rentabilité des processus", target_major_code: "GI" },
      { option_text: "Optimisation des rendements financiers", target_major_code: "GF" }
    ]
  },
  {
    question_text: "Quel mot-clé définit le mieux votre futur métier ?",
    order_index: 20,
    options: [
      { option_text: "Innovation logicielle", target_major_code: "IIR" },
      { option_text: "Automatisation intelligente", target_major_code: "GESI" },
      { option_text: "Intelligence Augmentée", target_major_code: "IAII" },
      { option_text: "Infrastructure durable", target_major_code: "GC" },
      { option_text: "Excellence opérationnelle", target_major_code: "GI" },
      { option_text: "Stratégie financière", target_major_code: "GF" }
    ]
  }
];

async function seed() {
  try {
    await mongoose.connect(process.env.MONGODB_URI);
    console.log('Connected to MongoDB for seeding...');
    
    await Question.deleteMany({});
    console.log('Cleared existing questions.');
    
    await Question.insertMany(sampleQuestions);
    console.log(`${sampleQuestions.length} questions inserted successfully.`);
    
    await mongoose.disconnect();
    console.log('Disconnected from MongoDB.');
  } catch (err) {
    console.error('Error during seeding:', err);
    process.exit(1);
  }
}

seed();
