import gurobi.*;

public class CommessoViaggiatore extends GRBCallback{

    private static final String LOG_FILENAME = "grafopoli.log";
    private static final String NUMERO_GRUPPO = "17";
    private static final String COMPONENTE_1 = "Castelnovo Luca";
    private static final String COMPONENTE_2 = "Soncina Daniele";
    private static final String MINORE = "<";
    private static final String MAGGIORE = ">";
    private static final String GRUPPO = "COPPIA " + MINORE + NUMERO_GRUPPO + MAGGIORE;
    private static final String COMPONENTI = "Componenti " + MINORE + COMPONENTE_1 + MAGGIORE + " " + MINORE + COMPONENTE_2 + MAGGIORE;
    private static final String FORMATO_NUMERO_CIFRE_DECIMALI = "%.4f";
    private static final String QUESITO_1 = "QUESITO I: ";
    private static final String QUESITO_2 = "QUESITO II: ";
    private static final String QUESITO_3 = "QUESITO III: ";
    private static final String QUADRA_APERTA = "[";
    private static final String QUADRA_CHIUSA = "]\n";
    private static final String FUNZIONE_OBIETTIVO = "funzione obiettivo = ";
    private static final String CICLO_OTTIMO1 = "ciclo ottimo 1 = ";
    private static final String CICLO_OTTIMO2 = "ciclo ottimo 2 = ";
    private static final String CICLO_OTTIMO3 = "ciclo ottimo 3 = ";

    private GRBVar[][] xij;

    final static int[][] costi =
            {
                    {0, 5, 7, 6, 6, 7, 6, 8, 8, 5, 3, 2, 11, 10, 3, 5, 7, 5, 6, 5, 5, 7, 5, 5, 5, 6, 5, 4, 4, 3, 5, 6, 7, 8, 7, 7, 2, 7, 6, 4, 5, 2, 7, 4, 7, 7, 5, 6},
                    {5, 0, 6, 4, 4, 7, 8, 8, 8, 10, 4, 7, 10, 9, 8, 4, 5, 5, 7, 6, 2, 2, 6, 8, 4, 6, 6, 7, 7, 6, 6, 6, 4, 7, 5, 8, 7, 2, 7, 5, 5, 7, 2, 5, 6, 6, 6, 6},
                    {7, 6, 0, 6, 6, 6, 10, 9, 9, 10, 6, 9, 9, 5, 6, 7, 7, 5, 4, 7, 4, 8, 4, 6, 7, 8, 6, 3, 7, 5, 5, 9, 5, 6, 5, 9, 8, 8, 9, 6, 7, 8, 7, 7, 7, 3, 8, 7},
                    {6, 4, 6, 0, 4, 6, 8, 7, 7, 10, 4, 4, 10, 9, 6, 7, 5, 5, 7, 5, 2, 6, 6, 8, 4, 8, 6, 7, 7, 6, 6, 9, 4, 7, 8, 8, 8, 6, 3, 5, 5, 7, 2, 8, 6, 6, 6, 6},
                    {6, 4, 6, 4, 0, 3, 8, 4, 7, 7, 4, 8, 6, 7, 8, 3, 6, 4, 5, 6, 2, 6, 6, 5, 6, 5, 6, 7, 9, 7, 5, 8, 6, 7, 6, 7, 4, 6, 5, 4, 5, 6, 4, 7, 2, 6, 6, 5},
                    {7, 7, 6, 6, 3, 0, 6, 7, 7, 4, 5, 8, 7, 6, 8, 6, 6, 4, 2, 6, 5, 9, 9, 2, 6, 4, 4, 4, 6, 4, 8, 9, 6, 4, 7, 6, 7, 8, 8, 7, 8, 5, 7, 7, 5, 6, 6, 6},
                    {6, 8, 10, 8, 8, 6, 0, 7, 2, 5, 8, 8, 5, 9, 7, 8, 9, 9, 8, 8, 6, 10, 9, 8, 10, 6, 10, 10, 9, 9, 7, 5, 6, 8, 9, 7, 4, 10, 6, 7, 8, 5, 8, 7, 7, 8, 10, 4},
                    {8, 8, 9, 7, 4, 7, 7, 0, 5, 8, 5, 8, 9, 7, 7, 6, 3, 4, 7, 5, 6, 7, 9, 5, 8, 5, 3, 8, 9, 8, 4, 8, 6, 8, 7, 8, 7, 9, 4, 8, 4, 8, 6, 10, 2, 6, 7, 5},
                    {8, 8, 9, 7, 7, 7, 2, 5, 0, 3, 8, 8, 7, 7, 5, 8, 8, 7, 8, 6, 8, 10, 7, 6, 8, 4, 8, 8, 7, 9, 9, 3, 4, 9, 8, 5, 6, 8, 4, 8, 9, 6, 6, 5, 5, 6, 10, 2},
                    {5, 10, 10, 10, 7, 4, 5, 8, 3, 0, 8, 7, 10, 10, 6, 6, 8, 8, 6, 6, 8, 12, 8, 6, 9, 5, 8, 8, 9, 7, 8, 6, 6, 8, 7, 2, 7, 8, 7, 5, 8, 3, 8, 5, 8, 8, 10, 5},
                    {3, 4, 6, 4, 4, 5, 8, 5, 8, 8, 0, 5, 8, 7, 6, 5, 6, 4, 3, 2, 2, 6, 6, 4, 7, 6, 2, 3, 7, 5, 3, 9, 5, 5, 6, 8, 5, 6, 4, 5, 4, 5, 6, 7, 6, 6, 2, 7},
                    {2, 7, 9, 4, 8, 8, 8, 8, 8, 7, 5, 0, 12, 11, 5, 7, 9, 7, 8, 6, 6, 9, 7, 6, 7, 5, 7, 6, 6, 5, 7, 6, 8, 9, 9, 8, 4, 7, 4, 6, 7, 4, 6, 4, 8, 7, 7, 7},
                    {11, 10, 9, 10, 6, 7, 5, 9, 7, 10, 8, 12, 0, 5, 11, 9, 8, 10, 5, 10, 8, 12, 12, 7, 10, 9, 9, 7, 10, 9, 9, 10, 9, 3, 11, 12, 9, 12, 8, 10, 7, 10, 8, 12, 8, 9, 9, 9},
                    {10, 9, 5, 9, 7, 6, 9, 7, 7, 10, 7, 11, 5, 0, 8, 6, 7, 9, 4, 9, 9, 11, 9, 6, 6, 8, 8, 6, 9, 8, 8, 7, 8, 2, 10, 11, 11, 11, 7, 11, 6, 9, 7, 8, 9, 8, 8, 9},
                    {3, 8, 6, 6, 8, 8, 7, 7, 5, 6, 6, 5, 11, 8, 0, 6, 8, 7, 8, 5, 6, 10, 2, 6, 6, 5, 8, 7, 7, 6, 8, 6, 5, 8, 9, 8, 5, 8, 3, 5, 8, 3, 7, 5, 6, 7, 8, 3},
                    {5, 4, 7, 7, 3, 6, 8, 6, 8, 6, 5, 7, 9, 6, 6, 0, 7, 5, 5, 3, 5, 6, 7, 6, 5, 8, 7, 7, 8, 6, 2, 7, 8, 7, 8, 8, 5, 6, 4, 5, 4, 3, 6, 5, 5, 7, 7, 8},
                    {7, 5, 7, 5, 6, 6, 9, 3, 8, 8, 6, 9, 8, 7, 8, 7, 0, 2, 7, 4, 7, 7, 10, 6, 5, 4, 6, 7, 8, 7, 7, 8, 5, 5, 6, 7, 5, 7, 6, 7, 7, 5, 3, 7, 4, 4, 8, 6},
                    {5, 5, 5, 5, 4, 4, 9, 4, 7, 8, 4, 7, 10, 9, 7, 5, 2, 0, 6, 2, 6, 7, 8, 6, 5, 5, 4, 6, 7, 5, 7, 8, 4, 7, 4, 8, 7, 7, 4, 7, 6, 5, 3, 6, 2, 2, 6, 5},
                    {6, 7, 4, 7, 5, 2, 8, 7, 8, 6, 3, 8, 5, 4, 8, 5, 7, 6, 0, 5, 5, 9, 8, 2, 6, 4, 4, 2, 5, 4, 6, 9, 4, 2, 8, 7, 8, 8, 7, 7, 6, 5, 6, 7, 7, 6, 4, 6},
                    {5, 6, 7, 5, 6, 6, 8, 5, 6, 6, 2, 6, 10, 9, 5, 3, 4, 2, 5, 0, 4, 6, 6, 6, 5, 7, 4, 4, 5, 3, 5, 7, 6, 7, 6, 8, 7, 8, 2, 5, 6, 3, 5, 5, 4, 4, 4, 7},
                    {5, 2, 4, 2, 2, 5, 6, 6, 8, 8, 2, 6, 8, 9, 6, 5, 7, 6, 5, 4, 0, 4, 4, 6, 6, 7, 4, 5, 9, 7, 5, 8, 6, 7, 7, 6, 6, 4, 5, 3, 3, 5, 4, 6, 4, 7, 4, 7},
                    {7, 2, 8, 6, 6, 9, 10, 7, 10, 12, 6, 9, 12, 11, 10, 6, 7, 7, 9, 6, 4, 0, 8, 10, 6, 8, 8, 9, 9, 8, 8, 8, 6, 9, 7, 10, 9, 4, 8, 7, 7, 9, 4, 7, 8, 8, 8, 8},
                    {5, 6, 4, 6, 6, 9, 9, 9, 7, 8, 6, 7, 12, 9, 2, 7, 10, 8, 8, 6, 4, 8, 0, 8, 8, 7, 8, 7, 9, 8, 9, 8, 7, 10, 9, 8, 7, 8, 5, 7, 7, 5, 8, 6, 8, 7, 8, 5},
                    {5, 8, 6, 8, 5, 2, 8, 5, 6, 6, 4, 6, 7, 6, 6, 6, 6, 6, 2, 6, 6, 10, 8, 0, 8, 2, 2, 4, 7, 6, 7, 7, 5, 4, 6, 5, 6, 6, 8, 5, 6, 3, 7, 5, 5, 7, 6, 4},
                    {5, 4, 7, 4, 6, 6, 10, 8, 8, 9, 7, 7, 10, 6, 6, 5, 5, 5, 6, 5, 6, 6, 8, 8, 0, 8, 9, 4, 4, 2, 7, 7, 4, 7, 7, 7, 7, 6, 7, 9, 9, 7, 2, 9, 7, 5, 6, 6},
                    {6, 6, 8, 8, 5, 4, 6, 5, 4, 5, 6, 5, 9, 8, 5, 8, 4, 5, 4, 7, 7, 8, 7, 2, 8, 0, 4, 6, 7, 8, 7, 5, 4, 6, 8, 3, 4, 4, 8, 6, 8, 5, 6, 7, 3, 6, 8, 2},
                    {5, 6, 6, 6, 6, 4, 10, 3, 8, 8, 2, 7, 9, 8, 8, 7, 6, 4, 4, 4, 4, 8, 8, 2, 9, 4, 0, 5, 8, 7, 5, 7, 7, 6, 4, 7, 7, 8, 6, 7, 6, 5, 7, 7, 5, 6, 4, 6},
                    {4, 7, 3, 7, 7, 4, 10, 8, 8, 8, 3, 6, 7, 6, 7, 7, 7, 6, 2, 4, 5, 9, 7, 4, 4, 6, 5, 0, 4, 2, 6, 9, 4, 4, 8, 7, 6, 9, 6, 8, 7, 6, 6, 8, 8, 6, 5, 6},
                    {4, 7, 7, 7, 9, 6, 9, 9, 7, 9, 7, 6, 10, 9, 7, 8, 8, 7, 5, 5, 9, 9, 9, 7, 4, 7, 8, 4, 0, 2, 9, 8, 3, 7, 7, 7, 6, 9, 7, 8, 9, 6, 5, 8, 8, 5, 8, 5},
                    {3, 6, 5, 6, 7, 4, 9, 8, 9, 7, 5, 5, 9, 8, 6, 6, 7, 5, 4, 3, 7, 8, 8, 6, 2, 8, 7, 2, 2, 0, 8, 9, 5, 6, 9, 5, 5, 8, 5, 7, 8, 5, 4, 7, 7, 7, 7, 7},
                    {5, 6, 5, 6, 5, 8, 7, 4, 9, 8, 3, 7, 9, 8, 8, 2, 7, 7, 6, 5, 5, 8, 9, 7, 7, 7, 5, 6, 9, 8, 0, 9, 7, 6, 6, 6, 3, 8, 6, 5, 2, 5, 8, 7, 6, 5, 5, 9},
                    {6, 6, 9, 9, 8, 9, 5, 8, 3, 6, 9, 6, 10, 7, 6, 7, 8, 8, 9, 7, 8, 8, 8, 7, 7, 5, 7, 9, 8, 9, 9, 0, 5, 9, 8, 8, 8, 5, 7, 5, 7, 4, 7, 2, 6, 6, 7, 3},
                    {7, 4, 5, 4, 6, 6, 6, 6, 4, 6, 5, 8, 9, 8, 5, 8, 5, 4, 4, 6, 6, 6, 7, 5, 4, 4, 7, 4, 3, 5, 7, 5, 0, 6, 4, 4, 5, 6, 7, 5, 8, 7, 2, 6, 5, 2, 7, 2},
                    {8, 7, 6, 7, 7, 4, 8, 8, 9, 8, 5, 9, 3, 2, 8, 7, 5, 7, 2, 7, 7, 9, 10, 4, 7, 6, 6, 4, 7, 6, 6, 9, 6, 0, 8, 9, 9, 9, 5, 9, 4, 7, 5, 9, 9, 7, 6, 8},
                    {7, 5, 5, 8, 6, 7, 9, 7, 8, 7, 6, 9, 11, 10, 9, 8, 6, 4, 8, 6, 7, 7, 9, 6, 7, 8, 4, 8, 7, 9, 6, 8, 4, 8, 0, 8, 5, 7, 8, 9, 4, 8, 6, 6, 6, 2, 8, 6},
                    {7, 8, 9, 8, 7, 6, 7, 8, 5, 2, 8, 8, 12, 11, 8, 8, 7, 8, 7, 8, 6, 10, 8, 5, 7, 3, 7, 7, 7, 5, 6, 8, 4, 9, 8, 0, 7, 7, 9, 3, 6, 5, 6, 6, 6, 6, 8, 5},
                    {2, 7, 8, 8, 4, 7, 4, 7, 6, 7, 5, 4, 9, 11, 5, 5, 5, 7, 8, 7, 6, 9, 7, 6, 7, 4, 7, 6, 6, 5, 3, 8, 5, 9, 5, 7, 0, 8, 8, 6, 5, 4, 7, 6, 6, 7, 7, 6},
                    {7, 2, 8, 6, 6, 8, 10, 9, 8, 8, 6, 7, 12, 11, 8, 6, 7, 7, 8, 8, 4, 4, 8, 6, 6, 4, 8, 9, 9, 8, 8, 5, 6, 9, 7, 7, 8, 0, 8, 6, 7, 5, 4, 3, 7, 7, 8, 6},
                    {6, 7, 9, 3, 5, 8, 6, 4, 4, 7, 4, 4, 8, 7, 3, 4, 6, 4, 7, 2, 5, 8, 5, 8, 7, 8, 6, 6, 7, 5, 6, 7, 7, 5, 8, 9, 8, 8, 0, 7, 8, 5, 5, 7, 6, 6, 6, 6},
                    {4, 5, 6, 5, 4, 7, 7, 8, 8, 5, 5, 6, 10, 11, 5, 5, 7, 7, 7, 5, 3, 7, 7, 5, 9, 6, 7, 8, 8, 7, 5, 5, 5, 9, 9, 3, 6, 6, 7, 0, 6, 2, 7, 3, 6, 7, 5, 7},
                    {5, 5, 7, 5, 5, 8, 8, 4, 9, 8, 4, 7, 7, 6, 8, 4, 7, 6, 6, 6, 3, 7, 7, 6, 9, 8, 6, 7, 9, 8, 2, 7, 8, 4, 4, 6, 5, 7, 8, 6, 0, 7, 7, 9, 6, 6, 6, 9},
                    {2, 7, 8, 7, 6, 5, 5, 8, 6, 3, 5, 4, 10, 9, 3, 3, 5, 5, 5, 3, 5, 9, 5, 3, 7, 5, 5, 6, 6, 5, 5, 4, 7, 7, 8, 5, 4, 5, 5, 2, 7, 0, 8, 2, 7, 6, 7, 6},
                    {7, 2, 7, 2, 4, 7, 8, 6, 6, 8, 6, 6, 8, 7, 7, 6, 3, 3, 6, 5, 4, 4, 8, 7, 2, 6, 7, 6, 5, 4, 8, 7, 2, 5, 6, 6, 7, 4, 5, 7, 7, 8, 0, 7, 5, 4, 7, 4},
                    {4, 5, 7, 8, 7, 7, 7, 10, 5, 5, 7, 4, 12, 8, 5, 5, 7, 6, 7, 5, 6, 7, 6, 5, 9, 7, 7, 8, 8, 7, 7, 2, 6, 9, 6, 6, 6, 3, 7, 3, 9, 2, 7, 0, 8, 4, 8, 5},
                    {7, 6, 7, 6, 2, 5, 7, 2, 5, 8, 6, 8, 8, 9, 6, 5, 4, 2, 7, 4, 4, 8, 8, 5, 7, 3, 5, 8, 8, 7, 6, 6, 5, 9, 6, 6, 6, 7, 6, 6, 6, 7, 5, 8, 0, 4, 8, 3},
                    {7, 6, 3, 6, 6, 6, 8, 6, 6, 8, 6, 7, 9, 8, 7, 7, 4, 2, 6, 4, 7, 8, 7, 7, 5, 6, 6, 6, 5, 7, 5, 6, 2, 7, 2, 6, 7, 7, 6, 7, 6, 6, 4, 4, 4, 0, 8, 4},
                    {5, 6, 8, 6, 6, 6, 10, 7, 10, 10, 2, 7, 9, 8, 8, 7, 8, 6, 4, 4, 4, 8, 8, 6, 6, 8, 4, 5, 8, 7, 5, 7, 7, 6, 8, 8, 7, 8, 6, 5, 6, 7, 7, 8, 8, 8, 0, 9},
                    {6, 6, 7, 6, 5, 6, 4, 5, 2, 5, 7, 7, 9, 9, 3, 8, 6, 5, 6, 7, 7, 8, 5, 4, 6, 2, 6, 6, 5, 7, 9, 3, 2, 8, 6, 5, 6, 6, 6, 7, 9, 6, 4, 5, 3, 4, 9, 0}
            };

    boolean second_answer = false;
    boolean third_answer = false;
    boolean first_iteration_second_answer = false;


    /**
     * metodo costruttore, vengono utilizzati durante le procedure di callback
     * @param new_xij   set di variabili corrente
     */
    public CommessoViaggiatore(GRBVar[][] new_xij)
    {
        xij = new_xij;
    }

    /**
     * metodo costruttore per alzare le flag corrispondenti all'esecuzione del secondo o del terzo quesito
     * @param new_xij   set di variabili corrente
     * @param second    flag di esecuzione secondo quesito
     * @param third     flag di esecuzione terzo quesito
     */
    public CommessoViaggiatore(GRBVar[][] new_xij, boolean second, boolean third)
    {
        xij = new_xij;
        second_answer = second;
        third_answer = third;
    }

    /**
     * metodo main, esegue il flusso di programma
     * @param args
     */
    public static void main(String[] args) {
        final int numero_nodi = 48;

        try
        {
            //Creo l'enviroment e imposto i settaggi desiderati
            GRBEnv env = new GRBEnv(LOG_FILENAME);
            setParams(env);
            //Creo il modello, le variabili e imposto la funzione obiettivo e i vincoli del problema per il primo quesito
            GRBModel model1 = new GRBModel(env);
            model1.set(GRB.IntParam.LazyConstraints, 1); //Attiva l'utilizzo delle LazyCostraints
            GRBVar[][] xij1 = addVars(model1, numero_nodi);
            addObjectiveFunction(model1, xij1, numero_nodi);
            addMaxOneVisitConstr(model1, xij1, numero_nodi);
            addPositiveConstr(model1, xij1, numero_nodi);
            addChooseAnotherNodeConstr(xij1, numero_nodi);
            model1.setCallback(new CommessoViaggiatore(xij1));
            //Creo il modello, le variabili e imposto la funzione obiettivo e i vincoli del problema per il secondo quesito
            resolve(model1);
            double objval1 = model1.get(GRB.DoubleAttr.ObjVal); //estraggo il valore della funzione obbiettivo del modello 1, necessario per il secondo quesito
            //aggiungo vincoli e risolvo modello del quesito due
            GRBModel model2 = new GRBModel(env);
            model2.set(GRB.IntParam.LazyConstraints, 1); //Attiva l'utilizzo delle LazyCostraints
            GRBVar[][] xij2 = addVars(model2, numero_nodi);
            addObjectiveFunction(model2, xij2, numero_nodi);
            addMaxOneVisitConstr(model2, xij2, numero_nodi);
            addPositiveConstr(model2, xij2, numero_nodi);
            addChooseAnotherNodeConstr(xij2, numero_nodi);
            addSecondAnswerConstr(model2, xij2, objval1, numero_nodi);
            model2.setCallback(new CommessoViaggiatore(xij2, true, false));
            resolve(model2);
            //Creo il modello, le variabili e imposto la funzione obiettivo e i vincoli del problema per il terzo quesito
            GRBModel model3 = new GRBModel(env);
            model3.set(GRB.IntParam.LazyConstraints, 1); //Attiva l'utilizzo delle LazyCostraints
            GRBVar[][] xij3 = addVars(model3, numero_nodi);
            addObjectiveFunction(model3, xij3, numero_nodi);
            addMaxOneVisitConstr(model3, xij3, numero_nodi);
            addPositiveConstr(model3, xij3, numero_nodi);
            addChooseAnotherNodeConstr(xij3, numero_nodi);
            addThirdAnswerConstr1(model3, xij3, numero_nodi);
            xij3[40][30].set(GRB.DoubleAttr.UB, 0.0); //per il terzo vincolo del terzo quesito, impongo che questo lato
                                                      // non sia percorribile se non avviene la condizione
            model3.setCallback(new CommessoViaggiatore(xij3, false, true));
            resolve(model3);
            //Visualizzo l'output con tutte le risposte ai quesiti
            showProjectGeneralities();
            showFirstAnswer(model1, xij1, numero_nodi);
            showSecondAnswer(model2, xij2, numero_nodi);
            showThirdAnswer(model3, xij3, numero_nodi);
            model1.dispose();
            model2.dispose();
            model3.dispose();
            env.dispose();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo per settare i parametri del solver Gurobi
     * @param env       environment del problema
     * @throws GRBException
     */
    private static void setParams(GRBEnv env)
            throws GRBException
    {
        env.set(GRB.IntParam.Presolve, 0); // Disattivo il presolve
        env.set(GRB.IntParam.Method, 0);
        env.set(GRB.DoubleParam.Heuristics, 0); //Disattivo l'utilizzo delle euristiche interne
    }

    /**
     * metodo che innesca la risoluzione del problema, e stampa a video lo stato della soluzione
     * @param model     il modello del problema da risolvere
     * @throws GRBException
     */
    private static void resolve(GRBModel model)
            throws GRBException
    // Metodo per la risoluzione del problema
    {
        model.optimize();

        int status = model.get(GRB.IntAttr.Status);
        System.out.println("\nStato Ottimizzazione: "+ status + "\n");
        // 2 soluzione ottima trovata
        // 3 non esiste soluzione ammissibile (infeasible)
        // 5 soluzione illimitata
        // 9 tempo limite raggiunto

    }

    /**
     * Metodo di callback per procedere con l'eliminazione dei subtour
     * NB: Questo metodo, la chiamata ai callback e tutto ciò che concerne i "lazy costraint" sono stati fortemente
     * ispirati dalla classe Tsp.java presente nella documentazione di gurobi, raggiungibile all'indirizzo:
     * https://www.gurobi.com/documentation/9.5/examples/tsp_java.html
     */
    protected void callback() {
        try {
            if (where == GRB.CB_MIPSOL) {
                // Found an integer feasible solution - does it visit every node?
                int n = xij.length;
                int[] tour = findsubtour(getSolution(xij));

                if (tour.length < n) {
                    // Add subtour elimination constraint
                    GRBLinExpr expr = new GRBLinExpr();
                    for (int i = 0; i < tour.length; i++)
                        for (int j = i+1; j < tour.length; j++)
                            expr.addTerm(1.0, xij[tour[i]][tour[j]]);
                    addLazy(expr, GRB.LESS_EQUAL, tour.length-1);
                }
                //per la seconda consegna
                if(second_answer)
                    xij[41][39].set(GRB.DoubleAttr.LB, 0.0);
                //per la terza consegna
                if(third_answer) {
                    addThirdAnswerConstr2();
                    addThirdAnswerConstr3();
                    addThirdAnswerConstr4();
                }
            }
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * metodo per individuare i subtour nella soluzione corrente
     * NB: Anche questo metodo è fortemente ispirato alla classe Tsp.java presente nella documentazione di Gurobi
     * @param sol       la soluzione corrente
     * @return
     */
    protected static int[] findsubtour(double[][] sol)
    {
        int n = sol.length;
        boolean[] seen = new boolean[n];
        int[] tour = new int[n];
        int bestind, bestlen;
        int i, node, len, start;

        for (i = 0; i < n; i++)
            seen[i] = false;

        start = 0;
        bestlen = n+1;
        bestind = -1;
        while (start < n) {
            for (node = 0; node < n; node++)
                if (!seen[node])
                    break;
            if (node == n)
                break;
            for (len = 0; len < n; len++) {
                tour[start+len] = node;
                seen[node] = true;
                for (i = 0; i < n; i++) {
                    if (sol[node][i] > 0.5 && !seen[i]) {
                        node = i;
                        break;
                    }
                }
                if (i == n) {
                    len++;
                    if (len < bestlen) {
                        bestlen = len;
                        bestind = start;
                    }
                    start += len;
                    break;
                }
            }
        }

        int[] result = new int[bestlen];
        for (i = 0; i < bestlen; i++)
            result[i] = tour[bestind+i];
        return result;
    }

    /**
     * Metodo per inserire nel modello la matrice delle variabili
     * @param model     il modello del problema da risolvere
     * @param n         numero archi
     * @return          set di variabili inserite
     * @throws GRBException
     */
    private static GRBVar[][] addVars(
            GRBModel model,
            int n)
            throws GRBException
    {
        GRBVar[][] xij = new GRBVar[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j <= i; j++) {
                xij[i][j] = model.addVar(0.0, 1.0, CommessoViaggiatore.costi[i][j],
                        GRB.BINARY,
                        "x" + i + "_" + j);
                xij[j][i] = xij[i][j];
            }
        return xij;
    }

    /**
     * Metodo per inserire la funzione obiettivo nel modello
     * @param model     il modello del problema
     * @param xij       variabili del problema
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void addObjectiveFunction(
            GRBModel model,
            GRBVar[][] xij,
            int n)
            throws GRBException
    {
        GRBLinExpr obj = new GRBLinExpr();
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                obj.addTerm(CommessoViaggiatore.costi[i][j], xij[i][j]);
            }
        }
        model.setObjective(obj);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE); //Imposto di trovare il minimo per la funzione obbiettivo
    }

    /**
     * Metodo per aggiungere il vincolo che impone che ogni nodo venga selezionato una sola volta come origine e
     * una sola volta come destinazione (in pratica, obbliga che un noda possa venire selezionato una singola volta)
     * @param model     il modello del problema da risolvere
     * @param xij       set di variabili del problema
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void addMaxOneVisitConstr(
            GRBModel model,
            GRBVar[][] xij,
            int n)
            throws GRBException
    {
         for (int i = 0; i < n; i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < n; j++) {
                expr.addTerm(1.0, xij[i][j]);
            }
            model.addConstr(expr, GRB.EQUAL, 2.0, "deg2_"+ i);
        }
    }


    /**
     * Metodo per inserire il vincolo che impedisce che un nodo scelga se stesso come successivo
     * @param xij       set di variabili del problema
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void addChooseAnotherNodeConstr(
            GRBVar[][] xij,
            int n)
            throws GRBException
    {
        for (int i = 0; i < n; i++)
            xij[i][i].set(GRB.DoubleAttr.UB, 0.0);
    }

    /**
     * Metodo per inserire il vincolo di positività sulle variabili
     * @param model     Il modello del problema da risolvere
     * @param xij       set di variabili del problema
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void addPositiveConstr(
            GRBModel model,
            GRBVar[][] xij,
            int n)
            throws GRBException
    {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                GRBLinExpr vincolo = new GRBLinExpr();
                vincolo.addTerm(1.0, xij[i][j]);
                model.addConstr(vincolo, GRB.GREATER_EQUAL, 0.0, "vincolo_variabili_maggiori_di_zero_" + i + "_" + j);
            }
        }
    }

    /**
     * Metodo per inserire un vincolo per il secondo quesito, che impone che il valore della funzione obiettivo sia
     * uguale quello del primo quesito
     * Viene imposto che il lower bound dell'ultimo nodo selezionato dal ciclo del primo quesito sia a 1, in modo che
     * gurobi sia costretto a selezionare quello per primo. Questo viene annullato con la prima chiamata di callback,
     * divincolando gurobi a tenere selezionato quel nodo.
     * @param xij   set di vriabili del problema
     * @throws GRBException
     */
    private static void addSecondAnswerConstr(
            GRBModel model,
            GRBVar[][] xij,
            double obj1,
            int n
            )
            throws GRBException
    {
        xij[41][39].set(GRB.DoubleAttr.LB, 1.0);
        GRBLinExpr vincolo = new GRBLinExpr();
        for(int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                vincolo.addTerm(CommessoViaggiatore.costi[i][j], xij[i][j]);
        }
        model.addConstr(vincolo, GRB.EQUAL, obj1, "vincolo_obiettivo_uguale_al_primo_modello");
    }

    /**
     * metodo per il primo vincolo del terzo quesito
     * @param model     modello del terzo quesito
     * @param xij       set di variabili del problema
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void addThirdAnswerConstr1(GRBModel model,
                                              GRBVar[][] xij,
                                              int n)
            throws GRBException

    {
        int costi_prec_succ = 0;
        GRBLinExpr vincolo = new GRBLinExpr();
        for(int i = 0; i < n; i++) {
            costi_prec_succ += CommessoViaggiatore.costi[i][39];
            vincolo.addTerm(CommessoViaggiatore.costi[i][39], xij[i][39]);
        }
        for(int j = 0; j < n; j++) {
            costi_prec_succ += CommessoViaggiatore.costi[39][j];
            vincolo.addTerm(CommessoViaggiatore.costi[39][j], xij[39][j]);
        }
        double costo_vincolo = costi_prec_succ * 0.08;
        model.addConstr(vincolo, GRB.LESS_EQUAL, costo_vincolo, "primo_vincolo_terza_consegna");
    }

    /**
     * metodo per il secondo vincolo del terzo quesito
     * @throws GRBException
     */
    private void addThirdAnswerConstr2()
            throws GRBException
    {
        int n = 48;
        double[][] current_sol = getSolution(xij);
        if (current_sol[41][16] == 1.0)
        {
            GRBLinExpr vincolo = new GRBLinExpr();
            for(int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++)
                    vincolo.addTerm(CommessoViaggiatore.costi[i][j], xij[i][j]);
            }
            addLazy(vincolo, GRB.LESS_EQUAL, 131);
        }
    }

    /**
     * metodo per il terzo vincolo del terzo quesito
     * @throws GRBException
     */
    private void addThirdAnswerConstr3()
            throws GRBException
    {
        int n = 48;
        double[][] current_sol = getSolution(xij);
        //Sblocco l'upper bound dell'arco 40-30 solo se si verifica la condizione
        if (current_sol[24][11] == 1.0 && current_sol[26][30] == 1.0)
        {
            xij[40][30].set(GRB.DoubleAttr.UB, 1.0);
        }
    }

    /**
     * metodo per il quarto vincolo del terzo quesito
     * @throws GRBException
     */
    private void addThirdAnswerConstr4()
            throws GRBException
    {
        int n = 48;
        double[][] current_sol = getSolution(xij);
        int costo_penale = 0;
        if (current_sol[12][6] == 1.0 && current_sol[15][41] == 1.0 && current_sol[30][15] == 1.0)
        {
            GRBLinExpr vincolo = new GRBLinExpr();
            for(int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    vincolo.addTerm(CommessoViaggiatore.costi[i][j], xij[i][j]);
                    costo_penale += CommessoViaggiatore.costi[i][j];
                }
            }
            costo_penale += 6;
            addLazy(vincolo, GRB.GREATER_EQUAL, costo_penale);
        }
    }

    /**
     * metodo per stampare a video le generalità del progetto
     */
    private static void showProjectGeneralities()
    {
        //Mostra solo la prima intestazione dell'output
        System.out.println("\n" + GRUPPO + "\n" + COMPONENTI + "\n");
    }

    /**
     * metodo per la visualizzazione della risposta del primo quesito
     * @param model     il modello del problema del primo quesito
     * @param xij       set di variabili del primo quesito
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void showFirstAnswer (
            GRBModel model,
            GRBVar[][] xij,
            int n)
            throws GRBException
    {
        String funzione_obiettivo = String.format(FORMATO_NUMERO_CIFRE_DECIMALI, model.get(GRB.DoubleAttr.ObjVal));
        System.out.println(QUESITO_1);
        System.out.println(FUNZIONE_OBIETTIVO + " " + MINORE + funzione_obiettivo + MAGGIORE);
        System.out.print(CICLO_OTTIMO1 + QUADRA_APERTA);
        if (model.get(GRB.IntAttr.SolCount) > 0) {
            int[] tour = findsubtour(model.get(GRB.DoubleAttr.X, xij));
            int i = 0;
            assert tour.length == n;
            for (int j : tour)
            {
                i++;
                if (i == n)
                    System.out.print(j + QUADRA_CHIUSA);
                else
                    System.out.print(j + ", ");
            }
        }
    }

    /**
     * metodo per la visualizzazione della risposta del secondo quesito
     * @param model     il modello del problema del secondo quesito
     * @param xij       set di variabili del secondo quesito
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void showSecondAnswer(
            GRBModel model,
            GRBVar[][] xij,
            int n)
            throws GRBException
    {

        String funzione_obiettivo = String.format(FORMATO_NUMERO_CIFRE_DECIMALI, model.get(GRB.DoubleAttr.ObjVal));
        System.out.println(QUESITO_2);
        System.out.println(FUNZIONE_OBIETTIVO + " " + MINORE + funzione_obiettivo + MAGGIORE);
        System.out.print(CICLO_OTTIMO2 + QUADRA_APERTA);
        if (model.get(GRB.IntAttr.SolCount) > 0) {
            int[] tour = findsubtour(model.get(GRB.DoubleAttr.X, xij));
            int i = 0;
            assert tour.length == n;
            for (int j : tour)
            {
                i++;
                if (i == n)
                    System.out.print(j + QUADRA_CHIUSA);
                else
                    System.out.print(j + ", ");
            }
        }
    }

    /**
     * metodo per la visualizzazione della risposta del terzo quesito
     * @param model     il modello del problema del terzo quesito
     * @param xij       set di variabili del terzo quesito
     * @param n         numero nodi
     * @throws GRBException
     */
    private static void showThirdAnswer(
            GRBModel model,
            GRBVar[][] xij,
            int n)
            throws GRBException
    {
        String funzione_obiettivo = String.format(FORMATO_NUMERO_CIFRE_DECIMALI, model.get(GRB.DoubleAttr.ObjVal));
        System.out.println(QUESITO_3);
        System.out.println(FUNZIONE_OBIETTIVO + " " + MINORE + funzione_obiettivo + MAGGIORE);
        System.out.print(CICLO_OTTIMO3 + QUADRA_APERTA);
        if (model.get(GRB.IntAttr.SolCount) > 0) {
            int[] tour = findsubtour(model.get(GRB.DoubleAttr.X, xij));
            int i = 0;
            assert tour.length == n;
            for (int j : tour)
            {
                i++;
                if (i == n)
                    System.out.print(j + QUADRA_CHIUSA);
                else
                    System.out.print(j + ", ");
            }
        }
    }
}


