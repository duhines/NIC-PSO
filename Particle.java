/*
Authors:
	Dustin Hines, David Anderson, Duncan Gans
Course: Nature-inspired computation fall 2018
Date: 10/16/2018
Description:
	Particle Class for the PSO algorithm that implements PSO particle methods.
	Methods implement the following functionalities: 
		- Default constructor
		- Particle constructor
		- piecewise multiplication
		- piecewise addition
		- piecewise subtraction
		- scalar multiplication
		- generate u vectors in range 0 to given phi value
		- updating velocity based on PSO algorithm
		- updating particle location based on velocity
		- evaluating Rosenbrock, Ackley, and Rastrigin benchmark functions
	This version of PSO implements 3 benchmark functions for testing (Ackley,
	Rastrigin, and Rosenbrock) and 4 different neighborhood topologies (global,
	ring, von Neumann, and random).
*/


import java.util.Random;

public class Particle {

	public double[] location; //array of the particle of N dimension
	public double current_val; //value of benchmark at current location
	public double[] velocity; //vector of particle's velocity in N dimension
	public double[] p_best_location; //vector that points towards p_best
	public double p_best_value; //value of current p_best
	public String benchmark;
	public int dimension;
	public double v_max;
    Random random = new Random();
	
	public Particle() {}; // Default constructor; should never be used!

	// Constructor where dimension and type of problem is specified:
	public Particle (int dim, String bmark) {
		benchmark = bmark;
		dimension = dim;
		location = new double[dimension];
		velocity = new double[dimension];
		//initialize velocity max values based on benchmark function
		if (benchmark == "ack") {
			v_max = 32.768;
		}
		else if (benchmark == "ras") {
			v_max = 5.12;
		}
		else {
			v_max = 2.048;
		}
		// Fill velocity and location vectors with values in appropriate ranges:
		for (int i = 0; i < dimension; i++) {
			
			if (benchmark.equals("rok")) { // ranges for Rosenbrock
				// generate location values in range  [15.0, 30.0]
				// format: double result = start + (random.nextDouble() * (end - start));
				location[i] = 15.0 + (random.nextDouble() * (30.0 - 15.0));
				
				//generate velocity values in range [-2.0, 2.0]
				velocity[i] = -2.0 + (random.nextDouble() * (2.0 + 2.0));
			}
			else if (benchmark.equals("ras")) { // Ranges for Rastrigin
				//generate location values in range [2.56, 5.12]
				location[i] = 2.56 + (random.nextDouble() * (5.12 - 2.56));

				//generate velocity values in range [-2.0, 4.0]
				velocity[i] = -2.0 + (random.nextDouble() * (4.0 + 2.0));
			}
			else { // Ranges for Ackley
				//generate location values in range [16.0, 32.0]
				location[i] = 16.0 + (random.nextDouble() * (32.0 - 16.0));

				//generate velocity values in range [-2.0, 4.0]
				velocity[i] = -2.0 + (random.nextDouble() * (4.0 + 2.0));
			}
		}
		//set current_val to benchmark evaluation at current location
		eval();
		// p_best should be initialized as the starting location.
		p_best_location = location.clone();
		// Set p_best value to value at starting location:
		p_best_value = current_val;
	}

	// Helper function enabling us to do elementwise multiplication of arrays:
	private double[] piecewise_multiplication(double[] arr1, double[] arr2) {
		double[] product = new double[arr1.length];
		for (int i = 0; i < arr1.length; i++) {
			product[i] = arr1[i] * arr2[i];
		}
		return product;
	}

	// Helper function allowing us to do elementwise addition of arrays:
	private double[] piecewise_addition(double[] arr1, double[] arr2) {
		double[] sum = new double[arr1.length];
		for (int i = 0; i < arr1.length; i++) {
			sum[i] = arr1[i] + arr2[i];
		}
		return sum;
	}

	// Helper function allowing us to do elementwise subtraction of arrays:
	private double[] piecewise_subtraction(double[] arr1, double[] arr2) {
		double[] diff = new double[arr1.length];
		for (int i = 0; i < arr1.length; i++) {
			diff[i] = arr1[i] - arr2[i];
		}
		return diff;
	}

	// Helper function allowing us to do scalar multiplication of arrays:
	private double[] scalar_mult(double[] vector, double scalar) {
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vector[i] * scalar;
		}
		return vector;
	}

	// Generates random "u-vectors" or bias vectors to be used in PSO velocity update
	private double[] generate_u_vector(int arrLen, double upperBound) {
		double[] u_vector = new double[arrLen];
		for (int i = 0; i < arrLen; i++) {
			// Generate a random number in the range [0, upperBound]
			u_vector[i] = random.nextDouble() * upperBound;
		}
		return u_vector;
	}

	
	//Method to update the velocity based on the neighborhood best, personal best,
	//phi values (biases), and the dimensionality of the problem
	public void update_velocity(double[] g_best, double chi, double phi1, double phi2) {

		double[] u1 = generate_u_vector(dimension, phi1); // Generate vector with values from 0 to phi1
		double[] u2 = generate_u_vector(dimension, phi2); // Generate vector with values from 0 to phi2
	
		// Update velocity. Implements PSO Vi+1 update algorithm where:
		// Vi+1 = chi(Vi + u1*(Xi - Pi) + u2*(Xi - Ni)), where u1 and u2 are
		// bias vectors and chi is a constrictor.
		double[] g_best_weight = piecewise_multiplication(u2, piecewise_subtraction(g_best, location));
		double[] p_best_weight = piecewise_multiplication(u1, piecewise_subtraction(p_best_location, location));
		double[] g_and_p_best_weight = piecewise_addition(g_best_weight, p_best_weight);
		double[] unconstricted_velocity = piecewise_addition(velocity, g_and_p_best_weight);
		velocity = scalar_mult(unconstricted_velocity, chi);
		//constrain velocities to v_max values specified in constructor	
		for (int i = 0; i < velocity.length; i++) {
			if (velocity[i] > v_max) {velocity[i] = v_max;}
			if (velocity[i] < -1 * v_max) {velocity[i] = -1 * v_max;}
		}
	}
	
	//Given velocity and current location, update location
	public void update_location() {
		location = piecewise_addition(location, velocity);
	}

	//evaluate the ackly benchmark function with the particles current location
	public double eval_ackley() {
		double left_sum = 0;
		double right_sum = 0;
		for (int i = 0; i < dimension; i++) {
			left_sum += Math.pow(location[i], 2);
			right_sum += Math.cos(2.0*Math.PI*location[i]);
		}
		left_sum = left_sum / dimension;
		right_sum = right_sum / dimension;
		return -20 * Math.exp(-0.2 * Math.sqrt(left_sum)) - Math.exp(right_sum) + 20 + Math.E;
	}

	//evaluate the rosenbrock benchmark function with the particles current location
	public double eval_rosenbrock() {
		double sum = 0;
		for (int i = 0; i < dimension - 1; i++) {
			sum += 100*Math.pow(location[i+1] - Math.pow(location[i], 2), 2) + Math.pow(location[i] - 1, 2);
		}
		return sum;

	}

	//evaluate the rastrigin benchmark function with the particles current location
	public double eval_rastrigin() {
		double sum = 0;
		for (int i = 0; i < dimension - 1; i++) {
			sum += Math.pow(location[i], 2) - 10*Math.cos(2*Math.PI*location[i]) + 10;
		}
		return sum;
	}

	//wrapper function for the evaluate methods that calls the appropiate benchmark evaluation
	//and sets the particles current value to the result. 
	public void eval() {
		double results;
		if (benchmark.equals("rok")) {
			results = eval_rosenbrock();
		}
		else if (benchmark.equals("ras")) {
			results = eval_rastrigin();
		}
		else {
			results = eval_ackley();
		}
		//update p_best value and location if the result at this location is better
		//than the current p_best value
		if (results <= p_best_value) {
			p_best_location = location;
			p_best_value = results;
		}
		current_val = results;
	}
}