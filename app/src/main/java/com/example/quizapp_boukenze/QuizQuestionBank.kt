package com.example.quizapp_boukenze

import java.text.Normalizer
import java.util.Locale

object QuizQuestionBank {
    const val TARGET_QUESTION_COUNT = 20

    fun completeWithDefaults(remoteQuestions: List<QuestionWithheld>): List<QuestionWithheld> {
        val completed = remoteQuestions
            .sortedBy { it.question.order_index }
            .take(TARGET_QUESTION_COUNT)
            .toMutableList()

        val knownQuestions = completed
            .map { normalizeQuestionText(it.question.question_text) }
            .toMutableSet()

        for (fallbackQuestion in defaultQuestions()) {
            if (completed.size >= TARGET_QUESTION_COUNT) {
                break
            }

            val normalizedText = normalizeQuestionText(fallbackQuestion.question.question_text)
            if (knownQuestions.add(normalizedText)) {
                completed.add(fallbackQuestion)
            }
        }

        return completed
            .take(TARGET_QUESTION_COUNT)
            .mapIndexed { index, item ->
                item.copy(question = item.question.copy(order_index = index + 1))
            }
    }

    private fun defaultQuestions(): List<QuestionWithheld> = listOf(
        question(
            1,
            "Qu'est-ce qui vous passionne le plus ?",
            "Developpement d'applications et reseaux",
            "Systemes embarques et robotique",
            "Intelligence artificielle et data science",
            "Conception de batiments et infrastructures",
            "Optimisation de la production industrielle",
            "Analyse financiere et marches"
        ),
        question(
            2,
            "Dans quel type d'environnement aimeriez-vous travailler ?",
            "Une entreprise technologique ou une startup",
            "Un laboratoire d'automatisme ou une usine high-tech",
            "Un centre de recherche en IA ou Big Data",
            "Un chantier ou un bureau d'etudes",
            "Une grande usine ou une chaine logistique",
            "Une banque, assurance ou salle de marche"
        ),
        question(
            3,
            "Quel outil preferez-vous utiliser ?",
            "Un editeur de code et des outils reseau",
            "Un oscilloscope, des capteurs et des cartes electroniques",
            "Python, notebooks et frameworks de machine learning",
            "AutoCAD, Revit ou logiciels de calcul structure",
            "ERP, tableaux de bord et outils de planification",
            "Excel avance, BI et outils d'analyse financiere"
        ),
        question(
            4,
            "Quel probleme aimeriez-vous resoudre en priorite ?",
            "Securiser et connecter des applications",
            "Automatiser un systeme physique intelligent",
            "Predire des resultats avec des donnees",
            "Construire des ouvrages solides et durables",
            "Reduire les pertes dans une ligne de production",
            "Optimiser un budget ou un investissement"
        ),
        question(
            5,
            "Quelle activite vous motive le plus dans un projet ?",
            "Coder une solution fiable de bout en bout",
            "Assembler et tester un prototype technique",
            "Entrainer un modele et analyser ses performances",
            "Dessiner, dimensionner et verifier une structure",
            "Organiser les ressources et les operations",
            "Evaluer les couts, risques et gains"
        ),
        question(
            6,
            "Quel cours vous attire le plus ?",
            "Programmation avancee et reseaux",
            "Electronique de puissance et automatique",
            "Machine learning et statistiques",
            "Resistance des materiaux et beton arme",
            "Gestion de production et supply chain",
            "Finance d'entreprise et gestion des risques"
        ),
        question(
            7,
            "Face a une panne complexe, que faites-vous d'abord ?",
            "Lire les logs et isoler le bug logiciel",
            "Verifier les branchements, signaux et capteurs",
            "Comparer les donnees et chercher un motif",
            "Inspecter les contraintes et la stabilite",
            "Analyser le processus et le goulot d'etranglement",
            "Verifier les hypotheses de cout et de rentabilite"
        ),
        question(
            8,
            "Quel livrable preferez-vous presenter ?",
            "Une application fonctionnelle et documentee",
            "Un systeme automatise qui reagit en temps reel",
            "Un modele predictif avec resultats chiffres",
            "Un plan technique et une note de calcul",
            "Un processus optimise avec indicateurs de performance",
            "Une analyse financiere avec recommandations"
        ),
        question(
            9,
            "Quel type de donnees aimez-vous manipuler ?",
            "Requetes, API, paquets reseau et bases de donnees",
            "Mesures de capteurs, signaux et commandes",
            "Grands jeux de donnees, images ou textes",
            "Plans, charges, dimensions et materiaux",
            "Stocks, delais, flux et capacites",
            "Prix, bilans, ratios et previsions"
        ),
        question(
            10,
            "Dans une equipe, quel role prenez-vous naturellement ?",
            "Architecte technique de la solution logicielle",
            "Responsable integration materiel et automatisme",
            "Analyste data et experimentation IA",
            "Responsable conception et validation structure",
            "Coordinateur methode, qualite et planning",
            "Responsable budget, risque et decision economique"
        ),
        question(
            11,
            "Quel stage vous interesse le plus ?",
            "Developpeur backend, mobile ou cybersecurite",
            "Automaticien, roboticien ou ingenieur embarque",
            "Data scientist ou ingenieur IA",
            "Assistant ingenieur travaux ou bureau d'etudes",
            "Ingenieur methodes, qualite ou logistique",
            "Analyste financier ou controleur de gestion"
        ),
        question(
            12,
            "Quel resultat vous donne le plus de satisfaction ?",
            "Une plateforme rapide, stable et bien securisee",
            "Une machine qui execute automatiquement une tache",
            "Une prediction precise a partir de donnees complexes",
            "Un ouvrage bien dimensionne et conforme",
            "Une production plus fluide et moins couteuse",
            "Une decision rentable et bien argumentee"
        ),
        question(
            13,
            "Quel defi technologique vous attire ?",
            "Cloud, API, securite et architecture logicielle",
            "IoT, energie, automatismes et controle",
            "Vision par ordinateur, NLP et IA generative",
            "Villes intelligentes, infrastructures et durabilite",
            "Industrie 4.0, lean et transformation digitale",
            "Fintech, scoring, portefeuille et risque"
        ),
        question(
            14,
            "Comment preferez-vous apprendre ?",
            "En construisant des applications et en debuggant",
            "En manipulant des composants et des systemes reels",
            "En testant des hypotheses sur des donnees",
            "En reliant theorie, plans et contraintes physiques",
            "En simulant et ameliorant des processus",
            "En comparant scenarios, chiffres et impacts"
        ),
        question(
            15,
            "Quel environnement logiciel vous semble le plus naturel ?",
            "Git, IDE, terminaux et plateformes cloud",
            "PLC, Matlab/Simulink et outils electroniques",
            "Python, Jupyter, TensorFlow ou PyTorch",
            "AutoCAD, Revit, Robot ou logiciels BIM",
            "ERP, MES, Gantt et tableaux de bord",
            "Excel, Power BI, Bloomberg ou outils comptables"
        ),
        question(
            16,
            "Quelle contrainte vous stimule le plus ?",
            "Performance, securite et maintenabilite",
            "Precision, temps reel et fiabilite materielle",
            "Qualite des donnees et generalisation du modele",
            "Normes, securite et resistance",
            "Delais, couts, qualite et productivite",
            "Risque, liquidite et rentabilite"
        ),
        question(
            17,
            "Quel projet de fin d'annee choisiriez-vous ?",
            "Application mobile avec backend et authentification",
            "Robot suiveur de ligne ou systeme domotique",
            "Assistant intelligent base sur des donnees reelles",
            "Etude complete d'un batiment ou pont",
            "Optimisation d'une chaine de production",
            "Modele de prevision financiere et tableau de bord"
        ),
        question(
            18,
            "Quel indicateur regarderiez-vous en premier ?",
            "Temps de reponse, disponibilite et erreurs",
            "Tension, courant, precision et stabilite",
            "Accuracy, recall, loss et biais",
            "Charge, fleche, contrainte et coefficient de securite",
            "TRS, delai, stock et taux de defaut",
            "Marge, cash-flow, volatilite et rendement"
        ),
        question(
            19,
            "Quel impact professionnel recherchez-vous ?",
            "Creer des services numeriques utilises au quotidien",
            "Rendre les equipements plus autonomes et intelligents",
            "Transformer les donnees en decisions intelligentes",
            "Construire des espaces utiles et securises",
            "Ameliorer l'efficacite des entreprises industrielles",
            "Aider les organisations a mieux investir"
        ),
        question(
            20,
            "Quelle filiere vous semble la plus proche de votre personnalite ?",
            "IIR pour le logiciel, les systemes et les reseaux",
            "GESI pour l'electricite, l'automatisme et l'embarque",
            "IAII pour les donnees, l'IA et l'innovation",
            "GC pour la construction et les infrastructures",
            "GI pour l'organisation industrielle et l'optimisation",
            "GF pour la finance, l'analyse et la decision"
        )
    )

    private fun question(
        id: Int,
        text: String,
        iir: String,
        gesi: String,
        iaii: String,
        gc: String,
        gi: String,
        gf: String
    ): QuestionWithheld {
        val options = listOf(
            "IIR" to iir,
            "GESI" to gesi,
            "IAII" to iaii,
            "GC" to gc,
            "GI" to gi,
            "GF" to gf
        ).mapIndexed { index, (majorCode, optionText) ->
            QuestionOption(
                id = id * 10 + index + 1,
                question_id = id,
                option_text = optionText,
                target_major_code = majorCode
            )
        }

        return QuestionWithheld(
            question = Question(
                id = id,
                question_text = text,
                order_index = id
            ),
            options = options
        )
    }

    private fun normalizeQuestionText(value: String): String {
        val withoutAccents = Normalizer
            .normalize(value, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")

        return withoutAccents
            .lowercase(Locale.ROOT)
            .replace("[^a-z0-9]+".toRegex(), " ")
            .trim()
    }
}
