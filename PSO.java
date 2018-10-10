
public class PSO {
	double chi = .7298;
	double phi1 = 2.05;
	double phi2 = 2.05;
	int check_interval = 1000;
	public int find_best_particle(Particle[] swarm, int dimensions, String benchmark) {
		double [] curr_best_index = 0;
		double curr_best_val = swarm[0].eval(dimensions, benchmark);
		for (i = 1; i < swarm.length; i ++) {
			double particle_value = swarm[i].eval(dimensions, benchmark);
			if (particle_value < curr_best_val) {
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
	public double[] global_PSO(Particle[] swarm, int dimensions, String benchmark, int interations) {

		//initialize current best value found to best starting particle
		int index_of_g_best = find_best_particle(swarm, dimensions, benchmark);
		double[] g_best_location = swarm[index_of_best].location.clone();
		double g_best_val = swarm[index_of_best].p_best_value;

		//used for testing, we want the best solution so far every 1000 iterations
		double[] periodic_results = new double[Math.floor(iterations / check_interval)];
		periodic_results[0] = g_best_val;
		int curr_iteration = 1;

		while (curr_iteration <= iterations) {
			for (int i = 0, i < swarm.length; i ++) {
					swarm[i].update_velocity(g_best_location, chi, phi1, phi2, dimension);
					swarm[i].update_location();
			}
			int index_of_curr_best = find_best_particle(swarm, dimensions, benchmark);

			//update g_best values if we have found a new best
			if (swarm[index_of_curr_best].p_best_value > g_best_val) {
				index_of_g_best = index_of_curr_best;
				g_best_location = swarm[index_of_curr_best].location.clone();
				g_best_val = swarm[index_of_curr_best].p_best_value;
			}
			//periodically print some results and save results for testing
			if (curr_iteration % 1000 == 0) {
				periodic_results[iteration / 1000] = g_best_val;
				System.out.println("Iteration: %d - Best Value: %d", curr_iteration, g_best_val);
			}
			curr_iteration ++;
		}
		System.out.println("Final Solution: %d", g_best_val);
		return periodic_results;

	}
	/*
	* Implementation for the non-global topologies--we're going to keep track of the 
	* neighborhoods as a list of the neighborhoods for each particle and that would
	* be a huge waste for the global neighborhood, since all particles share the 
	* same neighborhood.  
	*/
	public double[] non_global_PSO(Particle[] swarm, int dimensions, String benchmark, int iterations) {
		//initialize current best value 
		int index_of_g_best = find_best_particle(swarm, dimensions, benchmark);
		double[] g_best_loc = swarm[index_of_best].location.clone();
		double g_best_val = swarm[index_of_best].eval();

		double[] periodic_results = new double[Math.floor(iterations / check_interval)];
		periodic_results[0] = g_best_val;

		double[] n_bests = new double[];
		int[][] neighborhoods = get_neighborhoods(swarm, topology);

		int curr_iteration = 1;
		while (curr_iteration <= iterations) {
			// TODOS
			//get n_best values
			//update velocities
			//update location
			//.2 chance to reset topology if its random
			//check to see if any current particles are better than the best_so_far
			int index_of_curr_best = find_best_particle(swarm, dimensions, benchmark);
			//update g_best values if we have found a new best
			if (swarm[index_of_curr_best].p_best_value > g_best_val) {
				index_of_g_best = index_of_curr_best;
				g_best_location = swarm[index_of_curr_best].location.clone();
				g_best_val = swarm[index_of_curr_best].p_best_value;
			}
			if (curr_iteration % 100 == 0) {
				periodic_results[iteration / 1000] = g_best_val;
				System.out.println("Iteration: %d - Best Value: %d", curr_iteration, best_val);
			}
			curr_iteration ++;

		}
	}

	public double[] init_PSO(int swarm_size, int iterations, int dimensions, String benchmark, String topology) {
		// Initialize an array of particles that will serve as our swarm:
		Particle[] swarm = new Particle[swarm_size];
		for (i = 0; i < swarm.length; i ++) {
			swarm[i] = new Particle(dimensions, benchmark);
		}
		
		//treat global vs non-global topologies differently since global is far simpler
		double results = new double[swarm_size / check_interval];
		if (topology == "gl") {
			results = global_PSO(swarm, dimensions, benchmark, iterations);
		}
		else {
			results = non_global_PSO(swarm, dimensions, benchmark, iterations);
		}
		//return results of PSO every check_interval(1000) iterations
		return results;
	}

	public static int[][] get_neighborhoods(Particle[] swarm, String topology) {
		//neighborhood codes to test: (gl, ri, vn, ra)
		//represent neighborhoods as a 2d array where each index corresponds to an array of the 
		// particles in the neighborhood of the particle with that particular index in the swarm
		
		//ring topology
		if (topology == "ri") {

		}
		else if (topology == "vn") {

		}
		else {

		}
	}

	public static double[] update_neighborhoods(Particle[] swarm, int[][] neighborhoods) {
		//given the neighborhoods, return array of doubles where the value at each index is
		//the neighborhood best for the neighborhood that the particle with that index is in
		if (topology == "gl") {

		}
	}
    public static void main(String[] args) {
        //each member of args is a command line argument
        System.out.println("PSO time!");
        swarm_size = args[0];
        iterations = args[1];
        dimensionality = args[2];
        benchmark = args[3];
        topology = args[4];

        run_PSO(swarm_size, iterations, dimensionality, benchmark, topology);
    }


}