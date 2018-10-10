/*
Particle Class for the PSO algorithm:
	includes:
		location

*/

//for an N dimentional problem:
public class Particle {

	public double[] location; //array of the particle of N dimensions
	public double[] velocity; //vector of particle's velocity in N dimensions
	public double[] p_best; // Vector that points towards p_best
	public double p_best_value; // Value of current p_best

	Random random = new Random();
	
	public Particle() {}; // Default constructor; should never be used!

	// Constructor where dimension and type of problem is specified:
	public Particle (int dimension, String benchmark) {
		location = new double[dimension];
		velocity = new double[dimension];

		// Fill velocity and location vectors with values in appropriate ranges:
		for (int i = 0; i < dimension; i ++) {
			
			if (benchmark == "rok") { // ranges for Rosenbrock
				// generate location values in range  [15.0, 30.0]
				// format: double result = start + (random.nextDouble() * (end - start));
				location[i] = 15.0 + (random.nextDouble() * (30.0 - 15.0));
				
				//generate velocity values in range [-2.0, 2.0]
				velocity[i] = -2.0 + (random.nextDouble() * (2.0 + 2.0));
			}
			else if (benchmark == "ras") { // Ranges for Rastrigin
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

		// p_best should be initialized as the starting location.
		p_best = location.clone();
		
		// Set p_best value to value at starting location:
		p_best_value = eval(dimension, benchmark);
	}

	// Helper function enabling us to do elementwise multiplication of arrays:
	private double[] piecewise_multiplication(double[] arr1, double[] arr2) {
		double[] product = new double[arr1.length];
		for (int i = 0; i < arr1.length; i ++) {
			product[i] = arr1[i] * arr2[i];
		}
		return product;
	}

	// Helper function allowing us to do elementwise addition of arrays:
	private double[] piecewise_addition(double[] arr1, double[] arr2) {
		double[] sum = new double[arr1.length];
		for (int i = 0; i < arr1.length; i ++) {
			sum[i] = arr1[i] + arr2[i];
		}
		return sum;
	}

	// Helper function allowing us to do elementwise subtraction of arrays:
	private double[] piecewise_subtraction(double[] arr1, double[] arr2) {
		double[] diff = new double[arr1.length];
		for (int i = 0; i < arr1.length; i ++) {
			diff[i] = arr1[i] - arr2[i];
		}
		return diff;
	}

	// Helper function allowing us to do scalar multiplication of arrays:
	private double[] scalar_mult(double[] arr1, double scalar) {
		for (int i = 0; i < arr1.length; i ++) {
			arr1[i] = arr1[i] * scalar;
		}
		return arr1;
	}

	// Generates random "u-vectors" or bias vectors to be used in PSO velocity update
	private double[] generate_u_vector(int arrLen, upperBound) {
		double[] u_vector = new double[arrLen];
		for (int i = 0; i < arrLen; i ++) {
			// Generate a random number in the range [0, upperBound]
			u_vector[i] = random.nextDouble() * upperBound;
		}
		return u_vector;
	}

	/**
	* Method to update the velocity based on the neighborhood best, personal best,
	* phi values (biases), and the dimensionality of the problem
	*/
	public void update_velocity(double[] g_best, double chi, double phi1, double phi2, int dimension) {
		double[] u1 = generate_u_vector(dimension, phi1); // Generate vector with values from 0 to phi1
		double[] u2 = generate_u_vector(dimension, phi2); // Generate vector with values from 0 to phi2

		// Update velocity. Implements PSO Vi+1 update algorithm where:
		// Vi+1 = chi(Vi + u1*(Xi - Pi) + u2*(Xi - Ni)), where u1 and u2 are
		// bias vectors and chi is a constrictor.
		double[] g_best_weight = piecewise_multiplication(u2, piecewise_subtraction(location, g_best));
		double[] p_best_weight = piecewise_multiplication(u1, piecewise_subtraction(location, p_best));
		double[] g_and_p_best_weight = piecewise_addition(g_best_weight, p_best_weight);
		double[] unconstricted_velocity = piecewise_addition(velocity, g_and_p_best_weight);
		velocity = scalar_multiplication(chi, unconstricted_velocity);		
	}

	/**
	* Given velocity and current location, update location
	*/
	public void update_location() {
		location = piecewise_addition(location, velocity);
	}

	public double eval_ackley(int dimensions) {
		double left_sum = 0;
		double right_sum = 0;
		for (int i = 0; i < dimensions; i ++) {
			left_sum += Math.pow(location[i], 2);
			right_sum += Math.cos(2*Math.PI*location[i]);
		}
		left_sum = left_sum / dimensions;
		right_sum = right_sum / dimensions;
		return -20 * Math.exp(-0.2 * Math.sqrt(left_sum)) - Math.exp(right_sum) + 20 + Math.E;
	}

	public double eval_rosenbrock(int dimensions) {
		double sum = 0;
		for (int i = 0; i < dimensions - 1; i ++) {
			sum += 100*(location[i+1] - Math.pow(location[i], 2) + Math.pow(location[i] - 1, 2));
		}
		return sum;
	}

	public double eval_rastrigin(int dimensions) {
		sum = 0;
		for (i = 0; i < dimensions; i ++) {
			sum += Math.pow(location[i], 2) - 10*Math.cos(2*Math.PI*location[i]) + 10;
		}
		return sum;
	}

	public double eval(int dimensions, String benchmark) {
		if (benchmark == "rok") {
			curr_value = eval_rosenbrock(dimensions);
		}
		else if (benchmark == "ras") {
			curr_value = eval_rastrigin(dimensions);
		}
		else if (benchmark == "ack") {
			curr_value = eval_ackley(dimensions);
		}
		//update p_best value and location
		if (curr_value > p_best_value) {
			p_best = location;
			p_best_value = curr_value;
		}
		return curr_value;
	}

}