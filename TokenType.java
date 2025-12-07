 public enum TokenType {
        // Mots-clés
        FOREACH, AS, IF, ELSE, WHILE, FOR, RETURN,TRY, CATCH, 
        ELSEIF, SWITCH, CASE, BREAK,  FUNCTION, CLASS,NEW, PUBLIC, PRIVATE,
         STATIC, FINALLY, ARRAY, LIST, ECHO, PRINT,

        // Mots-clés personnalisés
        mot_cle_perso,
        // Identifiants et littéraux
        VARIABLE, IDENTIFIANT, NOMBRE, CHAINE, TRUE, FALSE, NULL,

        // symboles
        PARENTHESE_OUVRANTE, PARENTHESE_FERMANTE,
        ACCOLADE_OUVRANTE, ACCOLADE_FERMANTE,
        CROCHET_OUVRANT, CROCHET_FERMANT,
        POINT_VIRGULE, VIRGULE, POINT,

        // Opérateurs arithmétiques
        AFFECTATION, INCREMENTATION, DECREMENTATION,
        EGALITE, DIFFERENT,
        INFERIEUR, SUPERIEUR,
        INFERIEUR_EGALE, SUPERIEUR_EGALE,
        PLUS, MOINS, FOIS, DIVISE, MODULO, PUISSANCE,
        // Opérateurs logiques
        ET_LOGIQUE, OU_LOGIQUE, NOT,

        // Fin
        TAG_OUVERTURE_PHP, 
    TAG_FERMETURE_PHP, 
        FIN,
        // erreur
        ERREUR
    }
