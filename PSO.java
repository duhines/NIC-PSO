/*
Authors:
	Dustin Hines, David Anderson, Duncan Gans
Course: Nature-inspired computation fall 2018
Date: 10/16/2018
Description:
	PSO Class for the PSO algorithm that implements PSO algorithm methods.
	Methods implement the following functionalities: 
		- find the best current particle value in the swarm
		- global PSO algorithm
		- non-global PSO algorithm
		- initialize PSO by creating a swarm of particles
		- get neighborhoods
		- update neighborhoods
		- main
*/

import java.util.Arrays;

public class PSO {
	//constants for the PSO algorithm
	static double chi = 0.7298;
	static double phi1 = 2.05;
	static double phi2 = 2.05;
	//check the current best value so far every X iterations
	static int check_interval = 1000;
	static int rand_neighborhood_size = 5;
	static double random_reset = .2;

	/*
	* Iterate through the particles in the swarm and return the index of the particle with the
	* best current location in the solution space.  
	*/
	public static int find_best_particle(Particle[] swarm) {
		int curr_best_index = 0;
		double curr_best_val = swarm[0].current_val;
		for(int i = 1; i < swarm.length; i++) {
			double particle_value = swarm[i].current_val;
			if(particle_value <= curr_best_val) {
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
	public static double[] global_PSO(Particle[] swarm, int iterations) {
		//keep track of the global best particle so far's location and value
		int index_of_g_best = find_best_particle(swarm);
		double[] g_best_location = swarm[index_of_g_best].location.clone();
		double g_best_val = swarm[index_of_g_best].p_best_value;

		//used for testing, we want the best solution so far every check_interval iterations
		double[] periodic_results = new double[(int)Math.floor(iterations / 
			check_interval) + 1];
		periodic_results[0] = g_best_val;
		
		int curr_iteration = 1;
		while(curr_iteration <= iterations) {
			//for each particle, update velocity, location, and current benchmark evaluation value
			for(int i = 0; i < swarm.length; i++) {
					swarm[i].update_velocity(g_best_location, chi, phi1, phi2);
					swarm[i].update_location();
					swarm[i].eval();
			}
			//find best current value amount particles and check against global best, 
			//update global best if necessary
			int index_of_curr_best = find_best_particle(swarm);
			if(swarm[index_of_curr_best].p_best_value < g_best_val) {
				index_of_g_best = index_of_curr_best;
				g_best_location = swarm[index_of_curr_best].location.clone();
				g_best_val = swarm[index_of_curr_best].p_best_value;
			}
			//periodically print some results and save results for testing
			if(curr_iteration % check_interval == 0) {
				periodic_results[curr_iteration / check_interval] = g_best_val;
				System.out.println("Iteration: " + curr_iteration + 
					" - Best Value: " + g_best_val);
			}
			curr_iteration ++;
		}
		System.out.println("Final Solution: " + g_best_val);
		//return the saved results
		return periodic_results;
	}

	/*
	* Implementation of PSO for the non-global topologies. The neighborhoods are represented
	* as a list of the indeces of the particles in the neighborhood for a given particle
	* with a certain index.  
	*/
	public static double[] non_global_PSO(Particle[] swarm, 
		int iterations, String topology) {

		//initialize current global best value 
		int index_of_g_best = find_best_particle(swarm);
		double[] g_best_loc = swarm[index_of_g_best].location.clone();
		double g_best_val = swarm[index_of_g_best].p_best_value;

		//initialize structure for keeping track of periodic results for testing
		double[] periodic_results = new double[(int)Math.floor(iterations / 
			check_interval) + 1];
		periodic_results[0] = g_best_val;

		//initialize structure for maintaining neighborhood information for each particle
		double[] n_best_values = new double[swarm.length];
		double[][] n_best_locations = new double[swarm.length][swarm[0].dimension];
		int[][] neighborhoods = get_neighborhoods(swarm, topology);

		int curr_iteration = 1;
		while(curr_iteration <= iterations) {
			//if random topology, reset at random reset chance
			double random_val = Math.random();
			if(topology.equals("ra") && random_val < random_reset) {
				neighborhoods = get_neighborhoods(swarm, "ra");
			}

			//reset n_best_values 	
			n_best_values = new double[swarm.length];
			//find new neighborhood best values for each particle
			update_neighborhoods(swarm, neighborhoods, n_best_values, n_best_locations);
			for(int i = 0; i < swarm.length; i++) {
				swarm[i].update_velocity(n_best_locations[i], chi, phi1, phi2);
				swarm[i].update_location();
				swarm[i].eval();
			}
	
			int index_of_curr_best = find_best_particle(swarm);
			//update g_best values if we have found a new best
			if(swarm[index_of_curr_best].p_best_value < g_best_val) {
				System.out.println("Best Value is: " +  
					swarm[index_of_curr_best].p_best_value);
				index_of_g_best = index_of_curr_best;
				g_best_loc = swarm[index_of_curr_best].location.clone();
				g_best_val = swarm[index_of_curr_best].p_best_value;
			}
			if(curr_iteration % check_interval == 0) {
				periodic_results[(curr_iteration / check_interval)] = g_best_val;
				System.out.println("Iteration: " + curr_iteration + 
					" - Best Value: " + g_best_val);
			}
			curr_iteration ++;
		}
		return periodic_results;
	}
	
	public static double[] init_PSO(int swarm_size, int iterations, int dimensions, 
		String benchmark, String topology) {

		// Initialize an array of particles that will serve as our swarm:
		Particle[] swarm = new Particle[swarm_size];
		for(int i = 0; i < swarm.length; i++) {
			swarm[i] = new Particle(dimensions, benchmark);
		}
		//treat global vs non-global topologies differently since global is far simpler
		double[] results = new double[swarm_size / check_interval];
		if(topology.equals("gl")) {
			results = global_PSO(swarm, iterations);
		}
		else {
			results = non_global_PSO(swarm, iterations, topology);
		}
		//return results of PSO every check_interval (1000) iterations
		return results;
	}

	public static int[][] get_neighborhoods(Particle[] swarm, String topology) {
		//neighborhood codes to test:(gl, ri, vn, ra)
		//represent neighborhoods as a 2d array where each index corresponds to an array of the 
		// particles in the neighborhood of the particle with that particular index in the swarm

		//ring topology
		if(topology.equals("ri")) {
			int[][] neighborhoods = new int[swarm.length][3];
			for(int i = 0; i < swarm.length; i++) {
				neighborhoods[i][0] = ((i - 1) + swarm.length) % swarm.length;
				neighborhoods[i][1] = i;
				neighborhoods[i][2] = (i + 1) % swarm.length;
			}
			return neighborhoods;
		}
		else if(topology.equals("vn")) {
			int[][] neighborhoods = new int[swarm.length][5];
			for(int i = 0; i < swarm.length; i++) {
				neighborhoods[i][0] =((i - 1) + swarm.length) % swarm.length;
				neighborhoods[i][1] = i;
				neighborhoods[i][2] =(i + 1) % swarm.length;
				neighborhoods[i][2] =((i -(int)Math.sqrt(swarm.length)) + 
					swarm.length) % swarm.length;
				neighborhoods[i][2] =(i + 
					(int)Math.sqrt(swarm.length)) % swarm.length;
			}
			return neighborhoods;
		}
		else {
			int[][] neighborhoods = new int[swarm.length][rand_neighborhood_size];
			for(int i = 0; i < swarm.length; i++) {
				for(int j = 0; j < rand_neighborhood_size; j++) {
					//https://stackoverflow.com/questions/1128723/how-to-determine-
					//whether-an-array-contains-a-particular-value-in-java
					int distinct = 0;
					int randNum = 0;
					while(distinct == 0) {
						randNum =(int)(Math.random()*(swarm.length));
						int duplicate = 0;
						for(int k = 0; k < j; k++) {
							if(randNum == neighborhoods[i][k]) {
								duplicate = 1;
							}
						}
						if(duplicate == 0) {distinct = 1;}
					}
					neighborhoods[i][j] = randNum;
				}
			}
			return neighborhoods;
		}
	}

	public static void update_neighborhoods(Particle[] swarm, int[][] neighborhoods, 
		double[] n_best_values, double[][] n_best_locations) {
		//given the neighborhoods, return array of doubles where the value at each index is
		//the neighborhood best for the neighborhood that the particle with that index is in
		for(int i = 0; i < swarm.length; i++) {
			//System.out.println("I is " + i);
			double currBest = swarm[neighborhoods[i][0]].p_best_value;
			int currBestIndex = 0;
			for(int j = 1; j < neighborhoods[i].length; j++) {
				if(swarm[neighborhoods[i][j]].p_best_value < currBest) {
					currBest = swarm[neighborhoods[i][j]].p_best_value;
					currBestIndex = neighborhoods[i][j];
				}
			}
			n_best_values[i] = currBest;
			n_best_locations[i] = swarm[currBestIndex].p_best_location;
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