import java.util.ArrayList;
import java.util.List;

public class AnalyseurSyntaxique {
    private List<Token> tokens; // liste des tokens à analyser
    private int i = 0;

    public AnalyseurSyntaxique(List<Token> tokensSource) {
        this.tokens = tokensSource;
    }

    public List<String> getErreurs() {
        return erreurs;
    }

    public void Z() {
        S();

        if (verifier(TokenType.FIN)) {
            if (erreurs.isEmpty()) {
                System.out.println("\n Analyse syntaxique réussie !");
            }

        } else {
            erreur("Fin de fichier attendue");

        }
    }

    public void S() {
        // La boucle continue tant qu'il y a des tokens ET qu'on n'est pas à la fin du
        // fichier.
        while (i < tokens.size() && tokens.get(i).type != TokenType.FIN) {

            TokenType typeCourant = tokens.get(i).type;

           
            if (tokens.get(i).type == TokenType.ACCOLADE_FERMANTE) {
                System.out.println("AVERTISSEMENT: Accolade fermante '}' inattendue trouvée ");
                i++;

            }

            Instruction();
        }
    }

    public boolean verifier(TokenType typeAttendu) {

        if (i >= tokens.size())
            return false;

        Token courant = tokens.get(i);

        if (courant.type == typeAttendu) {
            i++;
            return true;
        } else {
            return false;
        }
    }

    private List<String> erreurs = new ArrayList<>();

    private Token getErreurToken() {
        if (tokens.isEmpty()) {
            return null; // liste vide
        }
        if (i < tokens.size()) {
            return tokens.get(i); // token courant
        } else {
            return tokens.get(tokens.size() - 1); // dernier token
        }
    }

    public void erreur(String message) {
        Token erreurToken = getErreurToken();
        if (erreurToken == null) {
            erreurs.add("ERREUR SYNTAXIQUE : aucun token disponible (code vide ?)");

        }

        String msg = "ERREUR SYNTAXIQUE Ligne " + erreurToken.ligne + "\n" +
                "    Token trouvé : \"" + erreurToken.val + "\"\n" +
                "    Attendu      : " + message;

        erreurs.add(msg);
        System.out.println(msg); // affichage 
    }

    public void DeclarationClasse() {
        if (!verifier(TokenType.CLASS)) {
            erreur("Mot-clé 'class' attendu");

        }
        if (!verifier(TokenType.IDENTIFIANT)) {
            erreur("Nom de classe attendu");

        }
        Bloc(); // corps de la classe

        System.out.println("SUCCÈS: Classe déclarée correctement");
    }

    public void DeclarationMethode() {
        // Vérifie le mot-clé "function"
        if (!verifier(TokenType.FUNCTION)) {
            erreur("Mot-clé 'function' attendu");

        }

        // Vérifie le nom de la fonction (identifiant)
        if (!verifier(TokenType.IDENTIFIANT)) {
            erreur("Nom de fonction attendu après 'function'");

        }

        // Vérifie la parenthèse ouvrante
        if (!verifier(TokenType.PARENTHESE_OUVRANTE)) {
            erreur("Il manque '(' après le nom de la fonction");

        }

        // Paramètres optionnels (liste de variables séparées par des virgules)
        while (i < tokens.size() && tokens.get(i).type == TokenType.VARIABLE) {
            verifier(TokenType.VARIABLE);
            if (i < tokens.size() && tokens.get(i).type == TokenType.VIRGULE) {
                verifier(TokenType.VIRGULE);
            } else {
                break;
            }
        }

        // Vérifie la parenthèse fermante
        if (!verifier(TokenType.PARENTHESE_FERMANTE)) {
            erreur("Il manque ')' après la liste des paramètres");

        }

        // Corps de la fonction (bloc)
        Bloc();

        System.out.println("SUCCÈS:Méthode déclarée correctement ");
    }

    public void DeclarationVariable() {
        // Vérifie le mot-clé "var" ou directement une variable PHP
        if (!verifier(TokenType.VARIABLE)) {
            erreur("Déclaration de variable attendue (ex: )");

        }

        // Optionnel : initialisation avec '=' Expression
        if (i < tokens.size() && tokens.get(i).type == TokenType.AFFECTATION) {
            verifier(TokenType.AFFECTATION);
            Expression(); // analyse l'expression d'initialisation
        }

        // Fin obligatoire par ';'
        if (!verifier(TokenType.POINT_VIRGULE)) {
            erreur("Il manque ';' à la fin de la déclaration de variable");
        }

        System.out.println("SUCCÈS: Variable déclarée correctement ");

    }

    public void ExpressionPrime() {

        // Cas "+" Terme Expression’
        if (i < tokens.size() && tokens.get(i).type == TokenType.PLUS) {
            i++; // Consomme '+'
            Terme(); // Analyse le Terme suivant
            ExpressionPrime(); 
            return;
        }

        // Cas "-" Terme Expression’
        if (i < tokens.size() && tokens.get(i).type == TokenType.MOINS) {
            i++; // Consomme '-'
            Terme(); // Analyse le Terme suivant
            ExpressionPrime(); 
            return;
        }

        // Cas ϵ (epsilon) : rien à faire, on termine
    }

    public void Facteur() {
        if (i >= tokens.size()) {
            erreur("Facteur attendu mais fin de fichier atteinte");
            return;
        }

        TokenType typeCourant = tokens.get(i).type;

        // Cas NOMBRE, VARIABLE, CHAINE
        if (typeCourant == TokenType.NOMBRE ||
                typeCourant == TokenType.VARIABLE ||
                typeCourant == TokenType.CHAINE) {
            i++;
        }
        // Cas IDENTIFIANT (peut être simple ou appel de fonction)
        else if (typeCourant == TokenType.IDENTIFIANT) {
            i++; // consomme l'identifiant

            // Vérifie si c'est un appel de fonction
            if (i < tokens.size() && tokens.get(i).type == TokenType.PARENTHESE_OUVRANTE) {
                i++; // consomme "("

                // Arguments optionnels
                if (i < tokens.size() && tokens.get(i).type != TokenType.PARENTHESE_FERMANTE) {
                    Expression(); // premier argument
                    while (i < tokens.size() && tokens.get(i).type == TokenType.VIRGULE) {
                        i++; // consomme ","
                        Expression(); // argument suivant
                    }
                }

                if (!verifier(TokenType.PARENTHESE_FERMANTE)) {
                    erreur("Il manque ')' après l'appel de fonction");
                }
            }
        }
        // Cas ( Expression )
        else if (typeCourant == TokenType.PARENTHESE_OUVRANTE) {
            i++;
            Expression();
            if (!verifier(TokenType.PARENTHESE_FERMANTE)) {
                erreur("Il manque ) après l'expression");
            }
        }
        // Sinon erreur
        else {
            erreur("Facteur attendu : nombre, variable, identifiant, chaîne ou '('");
        }
    }

    public void Terme() {

        Facteur(); // Première partie : Facteur
        TermePrime(); // Puis la suite (Terme’)
    }

    public void TermePrime() {

        if (i < tokens.size() && tokens.get(i).type == TokenType.FOIS) {
            i++; // Consomme '*'
            Facteur();
            TermePrime(); 
            return;
        }

        if (i < tokens.size() && tokens.get(i).type == TokenType.DIVISE) {
            i++; // Consomme '/'
            Facteur();
            TermePrime();
            return;
        }
        // Cas epsilon (ϵ) : rien à faire
    }

    public void Expression() {

        Terme(); // Première partie : Terme
        ExpressionPrime(); // Puis la suite (Expression’)
    }

    

    public void TryCatch() {
        verifier(TokenType.TRY);
        Bloc(); // Bloc après TRY

        boolean catchTrouve = false;

        // Réintégration de la gestion du ou des CATCH (maintenant optionnelle)
        while (i < tokens.size() && tokens.get(i).type == TokenType.CATCH) {
         
            verifier(TokenType.CATCH);
            
            verifier(TokenType.PARENTHESE_OUVRANTE);
            verifier(TokenType.IDENTIFIANT); // Type (ex: Exception)
            verifier(TokenType.VARIABLE); // Variable (ex: $e)
            verifier(TokenType.PARENTHESE_FERMANTE);

            Bloc(); // Bloc du catch
            catchTrouve = true;
        }

        // Gestion du FINALLY (optionnelle)
        boolean finallyTrouve = false;
        if (i < tokens.size() && tokens.get(i).type == TokenType.FINALLY) {
            verifier(TokenType.FINALLY);
            Bloc();
            finallyTrouve = true;
        }

        // VÉRIFICATION D'ERREUR 
        // Il faut au moins un catch OU un finally
        if (!catchTrouve && !finallyTrouve) {
            erreur("Un bloc 'try' doit être suivi d'au moins un 'catch' ou un 'finally'.");
        } else {
            System.out.println("SUCCÈS: Bloc try/catch/finally correct");
        }
    }

    public void Bloc() {
        if (!verifier(TokenType.ACCOLADE_OUVRANTE)) {
            erreur("Il manque '{' pour ouvrir un bloc");

        }

        // Lire les instructions tant qu'il y a des tokens valides
        while (i < tokens.size()
                && tokens.get(i).type != TokenType.ACCOLADE_FERMANTE) {
            Instruction(); // analyse une instruction

        }

        if (!verifier(TokenType.ACCOLADE_FERMANTE)) {
            erreur("Il manque '}' pour fermer le bloc");
        }
    }

    public void Instruction() {

        if (i >= tokens.size()) {
            erreur("Fin de fichier inattendue");
            return;
        }

        TokenType typeCourant = tokens.get(i).type;

        if (typeCourant == TokenType.TRY) {
            TryCatch();
        } else if (typeCourant == TokenType.CLASS) {
            DeclarationClasse();
        } else if (typeCourant == TokenType.FUNCTION) {
            DeclarationMethode();
        } else if (typeCourant == TokenType.VARIABLE) {
            DeclarationVariable(); 
        } else if (typeCourant == TokenType.IDENTIFIANT) {

            Comparaison(); // Analyse l'expression ou la comparaison

            // Consommer le point-virgule après l'expression/appel
            if (!verifier(TokenType.POINT_VIRGULE)) {
                erreur("Il manque ';' à la fin de l'instruction d'expression/comparaison.");
            }

        } else {
            //  Tout le reste est ignoré
            ignorerBloc();
        }
    }

    public void ignorerBloc() {
        // Sauter la condition entre parenthèses si elle existe
        if (i < tokens.size() && tokens.get(i).type == TokenType.PARENTHESE_OUVRANTE) {
            int paren = 1;
            i++;
            while (i < tokens.size() && paren > 0) {
                if (tokens.get(i).type == TokenType.PARENTHESE_OUVRANTE)
                    paren++;
                else if (tokens.get(i).type == TokenType.PARENTHESE_FERMANTE)
                    paren--;
                i++;
            }
        }

        // Si bloc { ... }
        if (i < tokens.size() && tokens.get(i).type == TokenType.ACCOLADE_OUVRANTE) {
            int profondeur = 1;
            i++;
            while (i < tokens.size() && profondeur > 0) {
                if (tokens.get(i).type == TokenType.ACCOLADE_OUVRANTE)
                    profondeur++;
                else if (tokens.get(i).type == TokenType.ACCOLADE_FERMANTE)
                    profondeur--;
                i++;
            }
            return;
        }

        // Sinon, ignorer jusqu’au ;
        while (i < tokens.size() && tokens.get(i).type != TokenType.POINT_VIRGULE) {
            i++;
        }
        if (i < tokens.size() && tokens.get(i).type == TokenType.POINT_VIRGULE) {
            i++;
        }
    }

    public void Comparaison() {
        // Première expression
        Expression();

        // Vérifie la présence d'un opérateur de comparaison
        if (i < tokens.size() &&
                (tokens.get(i).type == TokenType.EGALITE ||
                        tokens.get(i).type == TokenType.DIFFERENT ||
                        tokens.get(i).type == TokenType.INFERIEUR ||
                        tokens.get(i).type == TokenType.SUPERIEUR ||
                        tokens.get(i).type == TokenType.INFERIEUR_EGALE ||
                        tokens.get(i).type == TokenType.SUPERIEUR_EGALE)) {

            i++; // Consomme l'opérateur

            // Deuxième expression
            Expression();

            System.out.println("SUCCÈS:Comparaison correcte");
        }

    }

}

