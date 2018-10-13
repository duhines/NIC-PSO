import java.util.Arrays;



public class PSO {
	static double chi = 0.7298;
	static double phi1 = 2.05;
	static double phi2 = 2.05;
	static int check_interval = 1000;
	static int rand_neighborhood_size = 5;
	static double random_reset = .2;

	public static int find_best_particle(Particle[] swarm, int dimensions, String benchmark) {
		int curr_best_index = 0;
		double curr_best_val = swarm[0].eval(dimensions, benchmark);
		for (int i = 1; i < swarm.length; i ++) {
			double particle_value = swarm[i].eval(dimensions, benchmark);
			if (particle_value <= curr_best_val) {
				curr_best_index = i;
				curr_best_val = particle_value;
			}
		}
		return curr_best_index;
	}

	/*
	* Global PSO is far simpler, so we're going to break it into a special case instead of 
	* mashing together the global and non-global topology implementations
	*/
	public static double[] global_PSO(Particle[] swarm, int dimensions, String benchmark, int iterations) {

		//initialize current best value found to best starting particle
		int index_of_g_best = find_best_particle(swarm, dimensions, benchmark);
		double[] g_best_location = swarm[index_of_g_best].location.clone();
		double g_best_val = swarm[index_of_g_best].p_best_value;

		//used for testing, we want the best solution so far every 1000 iterations
		double[] periodic_results = new double[(int)Math.floor(iterations / check_interval)+1];
		periodic_results[0] = g_best_val;
		int curr_iteration = 1;

		while (curr_iteration <= iterations) {

			for (int i = 0; i < swarm.length; i ++) {
					swarm[i].update_velocity(g_best_location, chi, phi1, phi2, dimensions);
					swarm[i].update_location();
			}

			int index_of_curr_best = find_best_particle(swarm, dimensions, benchmark);

			//update g_best values if we have found a new best
			if (swarm[index_of_curr_best].p_best_value < g_best_val) {
				index_of_g_best = index_of_curr_best;
				g_best_location = swarm[index_of_curr_best].location.clone();
				g_best_val = swarm[index_of_curr_best].p_best_value;
			}
			//periodically print some results and save results for testing
			if (curr_iteration % 1000 == 0) {
				periodic_results[curr_iteration / 1000] = g_best_val;
				System.out.println("Iteration: " + curr_iteration + " - Best Value: " + g_best_val);
			}
			curr_iteration ++;
		}
		System.out.println("Final Solution: " + g_best_val);
		return periodic_results;

	}
		/*
	* Implementation for the non-global topologies--we're going to keep track of the 
	* neighborhoods as a list of the neighborhoods for each particle and that would
	* be a huge waste for the global neighborhood, since all particles share the 
	* same neighborhood.  
	*/
	public static double[] non_global_PSO(Particle[] swarm, int dimensions, String benchmark, int iterations, String topology) {
		//initialize current best value 
		System.out.println("in non global");
		int index_of_g_best = find_best_particle(swarm, dimensions, benchmark);
		double[] g_best_loc = swarm[index_of_g_best].location.clone();
		double g_best_val = swarm[index_of_g_best].eval(dimensions, benchmark);
		double[] periodic_results = new double[(int)Math.floor(iterations / check_interval)+1];
		periodic_results[0] = g_best_val;
		double[] n_bests = new double[swarm.length];
		double[][] n_best_locs = new double[swarm.length][dimensions];
		int[][] neighborhoods = get_neighborhoods(swarm, topology);

		int curr_iteration = 1;
		while (curr_iteration <= iterations) {
			double rando = Math.random();
			if (topology.equals("ra") && rando < random_reset) {
				neighborhoods = get_neighborhoods(swarm, "ra");
			}	

			

			n_bests = new double[swarm.length];
			update_neighborhoods(swarm, neighborhoods, n_bests, n_best_locs, dimensions, benchmark);

			for (int i = 0; i < swarm.length; i++) {
				swarm[i].update_velocity(n_best_locs[i], chi, phi1, phi2, dimensions);
				swarm[i].update_location();
			}
	
			int index_of_curr_best = find_best_particle(swarm, dimensions, benchmark);
			//update g_best values if we have found a new best
			if (swarm[index_of_curr_best].p_best_value < g_best_val) {
				System.out.println("BEAT______________Value is: " +  swarm[index_of_curr_best].p_best_value);
				index_of_g_best = index_of_curr_best;
				g_best_loc = swarm[index_of_curr_best].location.clone();
				g_best_val = swarm[index_of_curr_best].p_best_value;
			}
			if (curr_iteration % 1000 == 0) {
				double sum = 0;
				for (int i = 0; i < swarm.length; i++) {
					sum += swarm[i].velocity[1];
				}
				System.out.println("Average Veloc:" + sum / swarm.length);
				System.out.println("ITER IS" + curr_iteration);
				periodic_results[(curr_iteration / 1000)] = g_best_val;
				System.out.println("Iteration: " + curr_iteration + " - Best Value: " + g_best_val);

				
			}
			curr_iteration ++;

		}
		return periodic_results;
	}
	public static double[] init_PSO(int swarm_size, int iterations, int dimensions, String benchmark, String topology) {
		// Initialize an array of particles that will serve as our swarm:
		Particle[] swarm = new Particle[swarm_size];
		for (int i = 0; i < swarm.length; i ++) {
			swarm[i] = new Particle(dimensions, benchmark);
		}
		System.out.println(swarm.length);
		//treat global vs non-global topologies differently since global is far simpler
		double[] results = new double[swarm_size / check_interval];
		if (topology.equals("gl")) {
			results = global_PSO(swarm, dimensions, benchmark, iterations);
		}
		else {
			results = non_global_PSO(swarm, dimensions, benchmark, iterations, topology);
		}
		//return results of PSO every check_interval(1000) iterations
		return results;
	}

	public static int[][] get_neighborhoods(Particle[] swarm, String topology) {
		//neighborhood codes to test: (gl, ri, vn, ra)
		//represent neighborhoods as a 2d array where each index corresponds to an array of the 
		// particles in the neighborhood of the particle with that particular index in the swarm

		//ring topology
		if (topology.equals("ri")) {
			int[][] neighborhoods = new int[swarm.length][3];
			for (int i = 0; i < swarm.length; i++) {
				neighborhoods[i][0] = ((i - 1) + swarm.length) % swarm.length;
				neighborhoods[i][1] = i;
				neighborhoods[i][2] = (i + 1) % swarm.length;
			}
			return neighborhoods;
		}
		else if (topology.equals("vn")) {
			int[][] neighborhoods = new int[swarm.length][5];
			for (int i = 0; i < swarm.length; i++) {
				neighborhoods[i][0] = ((i - 1) + swarm.length) % swarm.length;
				neighborhoods[i][1] = i;
				neighborhoods[i][2] = (i + 1) % swarm.length;
				neighborhoods[i][2] = ((i - (int)Math.sqrt(swarm.length))+ swarm.length) % swarm.length;
				neighborhoods[i][2] = (i + (int)Math.sqrt(swarm.length)) % swarm.length;
			}
			return neighborhoods;
		}
		else {
			int[][] neighborhoods = new int[swarm.length][rand_neighborhood_size];
			for (int i = 0; i < swarm.length; i++) {
				for (int j = 0; j < rand_neighborhood_size; j++) {

					//https://stackoverflow.com/questions/1128723/how-to-determine-whether-an-array-contains-a-particular-value-in-java
					int distinct = 0;
					int randNum = 0;
					while (distinct == 0) {
						randNum = (int)(Math.random()*(swarm.length));
						int duplicate = 0;
						for (int k = 0; k < j; k++) {
							if (randNum == neighborhoods[i][k]) {
								duplicate = 1;
							}
						}
						if (duplicate == 0) {distinct = 1;}
					}
					neighborhoods[i][j] = randNum;
				}
			}
			return neighborhoods;
		}
	}
	public static void update_neighborhoods(Particle[] swarm, int[][] neighborhoods, double[] n_bests, double[][] n_best_locs, int dimensions, String benchmark) {
		//given the neighborhoods, return array of doubles where the value at each index is
		//the neighborhood best for the neighborhood that the particle with that index is in
		for (int i = 0; i < swarm.length; i++) {
			//System.out.println("I is " + i);
			double currBest = swarm[neighborhoods[i][0]].eval(dimensions, benchmark);
			int currBestIndex = 0;
			for (int j = 1; j < neighborhoods[i].length; j++) {

				if (swarm[neighborhoods[i][j]].eval(dimensions, benchmark) < currBest) {
					currBest = swarm[neighborhoods[i][j]].eval(dimensions, benchmark);
					currBestIndex = neighborhoods[i][j];
				}
			}
			n_bests[i] = currBest;
			n_best_locs[i] = swarm[currBestIndex].location;
		}
	}
    public static void main(String[] args) {
        //each member of args is a command line argument
        System.out.println("PSO time!");
        int swarm_size = Integer.parseInt(args[0]);
        int iterations = Integer.parseInt(args[1]);
        int dimensionality = Integer.parseInt(args[2]);
        String benchmark = args[3];
        String topology = args[4];

        init_PSO(swarm_size, iterations, dimensionality, benchmark, topology);
    }


}