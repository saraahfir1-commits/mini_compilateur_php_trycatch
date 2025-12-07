import java.util.ArrayList;
import java.util.List;

public class AnalyseurLexical {
    private String code;
    private int pos = 0;
    private int ligne = 1;
    private List<Token> tokens; // Liste pour stocker les tokens
    private List<String> errors = new ArrayList<>(); // Liste pour stocker les erreurs
    private static final char EOF = '\0';

    public AnalyseurLexical(String code) {
        this.code = code;
        tokens = new ArrayList<>();
    }

    private char lireCaractere() {
        try {
            return code.charAt(pos);
        } catch (IndexOutOfBoundsException e) {
            return EOF;
        }
    }

    private char regarderSuivant() {
        try {
            return code.charAt(pos + 1);
        } catch (IndexOutOfBoundsException e) {
            return EOF;
        }
    }

    public List<Token> genererTokens() {
        char caractere = lireCaractere();

        while (caractere != EOF) {
            // 1. espaces et commentaire
            if (caractere == ' ' || caractere == '\t' || caractere == '\r') {
                pos++;
                caractere = lireCaractere(); // avancer
            } else if (caractere == '\n') {
                ligne++;
                pos++;
                caractere = lireCaractere(); // avancer
            }

            else if (caractere == '/' && regarderSuivant() == '/') {
                // Ignorer jusqu'à la fin de la ligne
                while (lireCaractere() != '\n' && lireCaractere() != EOF) {
                    pos++;
                }
                caractere = lireCaractere(); // avancer
            } else if (caractere == '#') {
                // Ignorer jusqu'à la fin de la ligne
                while (lireCaractere() != '\n' && lireCaractere() != EOF) {
                    pos++;
                }
                caractere = lireCaractere(); // avancer
            } else if (caractere == '/' && regarderSuivant() == '*') {
                // Ignorer jusqu'à la fermeture */
                pos += 2; // sauter "/*"
                while (!(lireCaractere() == '*' && regarderSuivant() == '/') && lireCaractere() != EOF) {
                    if (lireCaractere() == '\n')
                        ligne++; // compter les lignes
                    pos++;
                }
                if (lireCaractere() == '*' && regarderSuivant() == '/') {
                    pos += 2; // sauter "*/"
                }
                caractere = lireCaractere(); // avancer
            }

            // 2. Variables
            else if (caractere == '$') {
                lireVariable();
                caractere = lireCaractere(); // avancer
            }
            // 3. Mots
            else if (Lettre(caractere)) {
                lireMot();
                caractere = lireCaractere(); // avancer
            }
            // 4. Nombres
            else if (Chiffre(caractere)) {
                lireNombre();
                caractere = lireCaractere(); // avancer
            }
            // 5. Symboles et Opérateurs
            else {
                if (caractere == '{') {
                    ajouterToken(TokenType.ACCOLADE_OUVRANTE, "{");
                    pos++;
                    caractere = lireCaractere(); // avancer
                } else if (caractere == '}') {
                    ajouterToken(TokenType.ACCOLADE_FERMANTE, "}");
                    pos++;
                    caractere = lireCaractere(); // avancer
                } else if (caractere == '(') {
                    ajouterToken(TokenType.PARENTHESE_OUVRANTE, "(");
                    pos++;
                    caractere = lireCaractere(); // avancer
                } else if (caractere == ')') {
                    ajouterToken(TokenType.PARENTHESE_FERMANTE, ")");
                    pos++;
                    caractere = lireCaractere(); // avancer
                } else if (caractere == ';') {
                    ajouterToken(TokenType.POINT_VIRGULE, ";");
                    pos++;
                    caractere = lireCaractere(); // avancer

                } else if (caractere == ',') {
                    ajouterToken(TokenType.VIRGULE, ",");
                    pos++;
                    caractere = lireCaractere(); // avancer
                } else if (caractere == '.') {
                    ajouterToken(TokenType.POINT, ".");
                    pos++;
                    caractere = lireCaractere(); // avancer
                }

                else if (caractere == '[') {
                    ajouterToken(TokenType.CROCHET_OUVRANT, "[");
                    pos++;
                    caractere = lireCaractere(); // avancer
                } else if (caractere == ']') {
                    ajouterToken(TokenType.CROCHET_FERMANT, "]");
                    pos++;
                    caractere = lireCaractere(); // avancer
                }

                // PLUS (+) et INCREMENTATION (++)
                else if (caractere == '+') {
                    if (regarderSuivant() == '+') {
                        ajouterToken(TokenType.INCREMENTATION, "++");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterToken(TokenType.PLUS, "+"); // Ajouté !
                        pos++;
                        caractere = lireCaractere(); // avancer
                    }
                }
                // MOINS (-) et DECREMENTATION (--)
                else if (caractere == '-') {
                    if (regarderSuivant() == '-') {
                        ajouterToken(TokenType.DECREMENTATION, "--");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterToken(TokenType.MOINS, "-"); // Ajouté
                        pos++;
                        caractere = lireCaractere(); // avancer
                    }
                }
                // FOIS (*)
                else if (caractere == '*') {
                    ajouterToken(TokenType.FOIS, "*"); // Ajouté
                    pos++;
                    caractere = lireCaractere(); // avancer
                }
                // DIVISION (/)
                else if (caractere == '/') {

                    ajouterToken(TokenType.DIVISE, "/"); // Ajouté
                    pos++;
                    caractere = lireCaractere(); // avancer
                }
                // EGAL (=) et EGALITE (==)
                else if (caractere == '=') {
                    if (regarderSuivant() == '=') {
                        ajouterToken(TokenType.EGALITE, "==");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterToken(TokenType.AFFECTATION, "=");
                        pos++;
                        caractere = lireCaractere(); // avancer
                    }
                }
                // Inférieur (<) et Inférieur ou égal (<=)
                else if (caractere == '<') {
                    if (regarderSuivant() == '=') {
                        ajouterToken(TokenType.INFERIEUR_EGALE, "<=");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterToken(TokenType.INFERIEUR, "<");
                        pos++;
                        caractere = lireCaractere(); // avancer

                    }
                }

                // Supérieur (>) et Supérieur ou égal (>=)
                else if (caractere == '>') {
                    if (regarderSuivant() == '=') {
                        ajouterToken(TokenType.SUPERIEUR_EGALE, ">=");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterToken(TokenType.SUPERIEUR, ">");
                        pos++;
                        caractere = lireCaractere(); // avancer
                    }
                }

                // DIFFÉRENT (!) et NOT (!=)
                else if (caractere == '!') {
                    if (regarderSuivant() == '=') {
                        ajouterToken(TokenType.DIFFERENT, "!=");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterToken(TokenType.NOT, "!");
                        pos++;
                        caractere = lireCaractere(); // avancer
                    }

                }
                // ET logique (&&)
                else if (caractere == '&') {
                    if (regarderSuivant() == '&') {
                        ajouterToken(TokenType.ET_LOGIQUE, "&&");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterErreur("Opérateur incomplet: & seul");
                        ajouterToken(TokenType.ERREUR, "&");
                        pos++;
                    }
                    caractere = lireCaractere(); // avancer
                }
                // OU logique (||)
                else if (caractere == '|') {
                    if (regarderSuivant() == '|') {
                        ajouterToken(TokenType.OU_LOGIQUE, "||");
                        pos = pos + 2;
                        caractere = lireCaractere(); // avancer
                    } else {
                        ajouterErreur("Opérateur incomplet: | seul");
                        ajouterToken(TokenType.ERREUR, "|");
                        pos++;
                    }
                    caractere = lireCaractere(); // avancer
                }
                // Reconnaissance du Tag de Fermeture PHP (?>)

                else if (caractere == '?' && regarderSuivant() == '>') {
                    ajouterToken(TokenType.TAG_FERMETURE_PHP, "?>");
                    pos = pos + 2;
                    caractere = lireCaractere(); // avancer
                }

                // Chaînes de caractères
                else if (caractere == '"' || caractere == '\'') {
                    lireChaine();
                    caractere = lireCaractere(); // avancer
                }

                else {
                    ajouterErreur("Caractère inconnu: " + caractere);
                    pos++;
                    ajouterToken(TokenType.ERREUR, String.valueOf(caractere));
                    caractere = lireCaractere(); // avancer
                }

            }
        }
        tokens.add(new Token(TokenType.FIN, "", ligne));
        return tokens;
    }

    private void lireVariable() {
        StringBuilder variableBuilder = new StringBuilder();

        // Ajouter le premier caractère '$'
        variableBuilder.append(lireCaractere());
        pos++;

        // Ajouter les lettres/chiffres qui suivent
        while (Lettre(lireCaractere()) || Chiffre(lireCaractere())) {
            variableBuilder.append(lireCaractere());
            pos++;
        }

        // Transformer en String final
        String variable = variableBuilder.toString();

        // Créer le token
        ajouterToken(TokenType.VARIABLE, variable);
    }

    private void lireChaine() {
        StringBuilder chaineBuilder = new StringBuilder();
        char guillemet = lireCaractere(); // " ou ' les deux sont acceptés en php
        pos++; // avancer après le guillemet ouvrant

        // Lire jusqu'à retrouver le même guillemet ou la fin du fichier
        while (lireCaractere() != guillemet && lireCaractere() != EOF && lireCaractere() != '\n') {
            chaineBuilder.append(lireCaractere());
            pos++;
        }

        // Vérifier si la chaîne est bien fermée
        if (lireCaractere() == guillemet) {
            pos++; // consommer le guillemet fermant
            ajouterToken(TokenType.CHAINE, chaineBuilder.toString());
        } else {
            ajouterErreur("Chaîne non terminée");
            ajouterToken(TokenType.ERREUR, chaineBuilder.toString());
        }
    }

    private void lireMot() {
        StringBuilder motBuilder = new StringBuilder();

        // Tant que le caractère est une lettre, on l'ajoute
        while (Lettre(lireCaractere())) {
            motBuilder.append(lireCaractere());
            pos++;
        }

        String mot = motBuilder.toString();

        // Vérification des mots-clés PHP
        if (mot.equals("foreach")) {
            ajouterToken(TokenType.FOREACH, mot);
        } else if (mot.equals("Sara") || mot.equals("AHFIR")) {
            ajouterToken(TokenType.mot_cle_perso, mot);
        } else if (mot.equals("if")) {
            ajouterToken(TokenType.IF, mot);
        } else if (mot.equals("as")) {
            ajouterToken(TokenType.AS, mot);
        } else if (mot.equals("else")) {
            ajouterToken(TokenType.ELSE, mot);
        } else if (mot.equals("elseif")) {
            ajouterToken(TokenType.ELSEIF, mot);
        } else if (mot.equals("while")) {
            ajouterToken(TokenType.WHILE, mot);
        } else if (mot.equals("switch")) {
            ajouterToken(TokenType.SWITCH, mot);
        } else if (mot.equals("for")) {
            ajouterToken(TokenType.FOR, mot);
        } else if (mot.equals("case")) {
            ajouterToken(TokenType.CASE, mot);
        } else if (mot.equals("break")) {
            ajouterToken(TokenType.BREAK, mot);
        }  else if (mot.equals("function")) {
            ajouterToken(TokenType.FUNCTION, mot);
        } else if (mot.equals("return")) {
            ajouterToken(TokenType.RETURN, mot);
        } else if (mot.equals("class")) {
            ajouterToken(TokenType.CLASS, mot);
        } else if (mot.equals("new")) {
            ajouterToken(TokenType.NEW, mot);
        } else if (mot.equals("public")) {
            ajouterToken(TokenType.PUBLIC, mot);
        } else if (mot.equals("private")) {
            ajouterToken(TokenType.PRIVATE, mot);
        } else if (mot.equals("static")) {
            ajouterToken(TokenType.STATIC, mot);
        }else if (mot.equals("try")) {
            ajouterToken(TokenType.TRY, mot);
        } else if (mot.equals("catch")) {
            ajouterToken(TokenType.CATCH, mot);
        } else if (mot.equals("finally")) {
            ajouterToken(TokenType.FINALLY, mot);
        } else if (mot.equals("list")) {
            ajouterToken(TokenType.LIST, mot);
        } else if (mot.equals("array")) {
            ajouterToken(TokenType.ARRAY, mot);
        } else if (mot.equals("echo")) {
            ajouterToken(TokenType.ECHO, mot);
        } else if (mot.equals("print")) {
            ajouterToken(TokenType.PRINT, mot);
        } else if (mot.equals("true")) {
            ajouterToken(TokenType.TRUE, mot);
        } else if (mot.equals("false")) {
            ajouterToken(TokenType.FALSE, mot);
        } else if (mot.equals("null")) {
            ajouterToken(TokenType.NULL, mot);
        } else {
            // Sinon, c'est un identifiant
            ajouterToken(TokenType.IDENTIFIANT, mot);
        }
    }

    private void lireNombre() {
        StringBuilder nombreBuilder = new StringBuilder();

        // Tant que le caractère est un chiffre, on l'ajoute
        while (Chiffre(lireCaractere())) {
            nombreBuilder.append(lireCaractere());
            pos++;
        }

        // Transformer en String final
        String nombre = nombreBuilder.toString();

        // Créer le token
        ajouterToken(TokenType.NOMBRE, nombre);
    }

    private void ajouterToken(TokenType type, String valeur) {
        tokens.add(new Token(type, valeur, this.ligne));
    }

    private boolean Lettre(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean Chiffre(char c) {
        return c >= '0' && c <= '9';
    }

    private void ajouterErreur(String msg) {
        errors.add("Erreur lexicale @ ligne " + ligne + " -> " + msg);
    }

    public List<String> getErrors() {
        return errors;
    }
}