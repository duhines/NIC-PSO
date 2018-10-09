
public class PSO {

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

	public void run_PSO(int swarm_size, int iterations, int dimensions, String benchmark, String topology) {
		// Initialize an array of particles that will serve as our swarm:
		Particle[] swarm = new Particle[swarm_size];
		for (i = 0; i < swarm.length; i ++) {
			swarm[i] = new Particle(dimensions, benchmark);
		}

		// Keep track of best soln. found:
		int index_of_best = find_best_particle(swarm, dimensions, benchmark);
		double[] best_loc = swarm[index_of_best].location.clone();
		double best_val = swarm[index_of_best].eval();

		int curr_iteration = 1;
		while (curr_iteration <= iterations) {
			

			if (curr_iteration % 100 == 0) {
				System.out.println("Iteration: %d - Best Value: %d", curr_iteration, best_val);
			}
			curr_iteration ++;

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