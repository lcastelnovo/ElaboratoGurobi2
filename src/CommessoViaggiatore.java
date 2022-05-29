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

    public CommessoViaggiatore(GRBVar[][] new_xij)
    {
        xij = new_xij;
    }

    public static void main(String[] args) {
        final int numero_archi = 48;

        
        try
        {
            //Creo l'enviroment e imposto i settaggi desiderati
            GRBEnv env = new GRBEnv(LOG_FILENAME);
            setParams(env);
            //Creo il modello, le variabili e imposto la funzione obiettivo e i vincoli del problema
            GRBModel model = new GRBModel(env);
            //model.set(GRB.IntParam.LazyConstraints, 1);
            GRBVar[][] xij = addVars(model, numero_archi);
            addObjectiveFunction(model, xij, numero_archi);
            addMaxOneVisitConstr(model, xij, numero_archi);
            addPositiveConstr(model, xij, numero_archi);
            for (int i = 0; i < numero_archi; i++)
                xij[i][i].set(GRB.DoubleAttr.UB, 0.0);
            model.setCallback(new CommessoViaggiatore(xij));
            //resolve(model);
            model.optimize();
            if (model.get(GRB.IntAttr.SolCount) > 0) {
                int[] tour = findsubtour(model.get(GRB.DoubleAttr.X, xij));
                assert tour.length == numero_archi;
                System.out.print("Tour: ");
                for (int i = 0; i < tour.length; i++)
                    System.out.print(String.valueOf(tour[i]) + " ");
                System.out.println();
            }
            for(GRBVar var : model.getVars())
            {
                //stampo il valore delle variabili all'ottimo
                if (var.get(GRB.DoubleAttr.X) == 1.000)
                    System.out.println(MINORE + var.get(GRB.StringAttr.VarName) + MAGGIORE + " = " + MINORE + String.format(FORMATO_NUMERO_CIFRE_DECIMALI, var.get(GRB.DoubleAttr.X)) + MAGGIORE);
            }
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    private static void setParams(GRBEnv env)
            throws GRBException // Metodo per settare i parametri del solver Gurobi
    {
        env.set(GRB.IntParam.Presolve, 0); // Disattivo il presolve
        //env.set(GRB.IntParam.Method, 0);
        env.set(GRB.DoubleParam.Heuristics, 0); //Disattivo l'utilizzo delle euristiche interne
    }

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
            }
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
            e.printStackTrace();
        }
    }

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
        node = 0;
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

    private static void showProjectGeneralities()
    {
        //Mostra solo la prima intestazione dell'output
        System.out.println("\n" + GRUPPO + "\n" + COMPONENTI + "\n");
    }

    /**
     * Metodo per inserire nel modello la matrice delle variabili
     * @param model     il modello del problema da risolvere
     * @param n         numero archi
     * @return          set di variabili inserite
     * @throws GRBException
     */
    private static GRBVar[][] addVars(GRBModel model, int n)
            throws GRBException
    {
        GRBVar[][] xij = new GRBVar[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                xij[i][j] = model.addVar(0.0, 1.0, CommessoViaggiatore.costi[i][j],
                        GRB.BINARY,
                        "x" + String.valueOf(i) + "_" + String.valueOf(j));
            }
        return xij;
    }

    /**
     * Metodo per inserire la funzione obiettivo nel modello
     * @param model     il modello del problema
     * @param xij       variabili del problema
     * @param n         numero archi
     * @throws GRBException
     */
    private static void addObjectiveFunction(GRBModel model,
                                             GRBVar[][] xij,
                                             int n)
            throws GRBException
    {
        GRBLinExpr obj = new GRBLinExpr();
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++) {

                obj.addTerm(CommessoViaggiatore.costi[i][j], xij[i][j]);
                }
            }
        model.set(GRB.IntParam.LazyConstraints, 1);
        model.setObjective(obj);
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE); //Imposto di trovare il minimo per la funzione obbiettivo
    }

    /**
     * Metodo per aggiungere il vincolo che impone che ogni nodo venga visitato una sola volta
     * @param model     il modello del problema da risolvere
     * @param xij       set di variabili del problema
     * @param n         numero archi
     * @throws GRBException
     */
    private static void addMaxOneVisitConstr(GRBModel model,
                                               GRBVar[][] xij,
                                               int n)
            throws GRBException
    {/*
       for (int i = 0; i < n; i++) {
            GRBLinExpr vincolo_righe = new GRBLinExpr();
            for (int j = 0; j < n; j++) {
                vincolo_righe.addTerm(1.0, xij[i][j]);
            };
            model.addConstr(vincolo_righe, GRB.EQUAL, 1.0, "vincolo_vertice_visitato_una_volta_destinazione_" + i);
        }
        for (int j = 0; j < n; j++) {
            GRBLinExpr vincolo_colonne = new GRBLinExpr();
            for (int i = 0; i < n; i++) {
                vincolo_colonne.addTerm(1.0, xij[i][j]);
            };
            model.addConstr(vincolo_colonne, GRB.EQUAL, 1.0, "vincolo_vertice_visitato_una_volta_origine_" + j);
        }*/
         for (int i = 0; i < n; i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < n; j++)
                expr.addTerm(1.0, xij[i][j]);
            model.addConstr(expr, GRB.EQUAL, 2.0, "deg2_"+String.valueOf(i));
        }
    }

    private static void addPositiveConstr(GRBModel model,
                                          GRBVar[][] xij,
                                          int n
                                          )
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
}


