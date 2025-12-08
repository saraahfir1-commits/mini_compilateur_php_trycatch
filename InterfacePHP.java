import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InterfacePHP extends JFrame {

    private JTextArea zoneCode;
    private JTextArea zoneResultat;
    private JButton btnAnalyser;
    private JButton btnEffacer;

    public InterfacePHP() {
        // Configuration de la fenêtre
        setTitle("Compilateur PHP (Try-Catch)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer à l'écran
        setLayout(new BorderLayout());

        // --- HAUT : Titre ---
        JLabel titre = new JLabel("Analyseur Lexical & Syntaxique PHP", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 20));
        titre.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titre, BorderLayout.NORTH);

        // --- CENTRE : Zones de texte (SplitPane) ---
        zoneCode = new JTextArea();
        zoneCode.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollCode = new JScrollPane(zoneCode);
        scrollCode.setBorder(new TitledBorder("Code PHP Source"));

        zoneResultat = new JTextArea();

        zoneCode.setText(
    "try {\n" +
    "    $resultat = division(10, 0); // provoque une exception\n" +
    "    echo \"Résultat: \" . $resultat . \"\\n\";\n" +
    "} catch (Exception $e) {\n" +
    "    echo \"Erreur attrapée: \" . $e->getMessage() . \"\\n\";\n" +
    "} finally {\n" +
    "    echo \"Bloc finally exécuté, nettoyage terminé.\\n\";\n" +
    "}\n"
);
        zoneResultat.setFont(new Font("Monospaced", Font.PLAIN, 14));
        zoneResultat.setEditable(false);
        zoneResultat.setBackground(new Color(245, 245, 245));
        zoneResultat.setForeground(Color.BLACK);
        JScrollPane scrollResultat = new JScrollPane(zoneResultat);
        scrollResultat.setBorder(new TitledBorder("Résultats de l'analyse"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollCode, scrollResultat);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // --- BAS : Boutons ---
        JPanel panelBoutons = new JPanel();

        btnAnalyser = new JButton("LANCER L'ANALYSE");
        btnAnalyser.setFont(new Font("Arial", Font.BOLD, 14));
        btnAnalyser.setBackground(new Color( 65, 105, 225));
        btnAnalyser.setForeground(Color.WHITE);

        btnEffacer = new JButton("Effacer tout");
        btnEffacer.setBackground(Color.LIGHT_GRAY);

        panelBoutons.add(btnEffacer);
        panelBoutons.add(btnAnalyser);
        add(panelBoutons, BorderLayout.SOUTH);

        // --- ACTIONS ---
        btnAnalyser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lancerAnalyse();
            }
        });

        btnEffacer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoneCode.setText("");
                zoneResultat.setText("");
            }
        });
    }

    private void lancerAnalyse() {
        String code = zoneCode.getText();
        zoneResultat.setText("");

        if (code.trim().isEmpty()) {
            zoneResultat.append("❌ Erreur : Veuillez entrer du code PHP.");
            return;
        }

        StringBuilder log = new StringBuilder();

        // ==========================================
        // 1. ANALYSE LEXICALE
        // ==========================================
        log.append("--- 1. ANALYSE LEXICALE ---\n");
        AnalyseurLexical lexer = new AnalyseurLexical(code);
        List<Token> tokens = lexer.genererTokens(); // ⚠️ méthode correcte

        for (Token t : tokens) {
            log.append(t.toString()).append("\n");
        }

        List<String> errLex = lexer.getErrors(); // ⚠️ méthode correcte
        if (!errLex.isEmpty()) {
            log.append("\n❌ ÉCHEC LEXICAL :\n");
            for (String err : errLex) {
                log.append(err).append("\n");
            }
            zoneResultat.setText(log.toString());
            zoneResultat.setForeground(Color.RED);
            return;
        } else {
            log.append("\n✅ Analyse Lexicale OK.\n");
        }

        // ==========================================
        // 2. ANALYSE SYNTAXIQUE
        // ==========================================
        log.append("\n--- 2. ANALYSE SYNTAXIQUE ---\n");

        AnalyseurSyntaxique parser = new AnalyseurSyntaxique(tokens);
        parser.Z();

        List<String> errSyn = parser.getErreurs(); // ⚠️ méthode correcte
        if (errSyn.isEmpty()) {
            log.append("✅ ANALYSE RÉUSSIE !\n");
            log.append("Structure Try-Catch correcte.");
            zoneResultat.setForeground(new Color(0, 100, 0));
        } else {
            log.append("❌ ÉCHEC SYNTAXIQUE :\n");
            for (String err : errSyn) {
                log.append(err).append("\n");
            }
            zoneResultat.setForeground(Color.RED);
        }

        zoneResultat.setText(log.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InterfacePHP().setVisible(true);
            }
        });
    }
}
