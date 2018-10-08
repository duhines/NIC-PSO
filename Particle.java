/*
Particle Class for the PSO algorithm:
	includes:
		location

*/

//for an N dimentional problem:
public class Particle {

	public double[] location; //array of the particle of N dimensions
	public double[] velocity; //vector of particle's velocity in N dimensions
	public double[] p_best;


	private double[] piecewise_multiplication(double[] arr1, double[] arr2) {
		double[] product = new double[arr1.length];
		for (int i = 0; i < arr1.length; i ++) {
			product[i] = arr1[i] * arr2[i];
		}
		return product;
	}

	private double[] piecewise_addition(double[] arr1, double[] arr2) {
		double[] sum = new double[arr1.length];
		for (int i = 0; i < arr1.length; i ++) {
			sum[i] = arr1[i] + arr2[i];
		}
		return sum;
	}

	private double[] piecewise_subtraction(double[] arr1, double[] arr2) {
		double[] diff = new double[arr1.length];
		for (int i = 0; i < arr1.length; i ++) {
			diff[i] = arr1[i] - arr2[i];
		}
		return diff;
	}

	private double[] scalar_mult(double[] arr1, double scalar) {
		for (int i = 0; i < arr1.length; i ++) {
			arr1[i] = arr1[i] * scalar;
		}
		return arr1;
	}

	private double[] generate_u_vector(int arrLen, upperBound) {
		double[] u_vector = new double[arrLen];
		for (int i = 0; i < arrLen; i ++) {
			// From dzone.com/articles/random-number-generation-in-java
			u_vector[i] = Math.random() * (upperBound + 1);
		}
		return u_vector;
	}

	/**
	* Method to update the velocity based on the neighborhood best, personal best,
	* phi values (biases), and the dimensionality of the problem
	*/
	public void update_velocity(g_best, inertia, phi1, phi2, dimension) {
		double[] u1 = generate_u_vector(dimension, phi1); // Generate vector with values from 0 to phi1
		double[] u2 = generate_u_vector(dimension, phi2); // Generate vector with values from 0 to phi2

		// Update velocity. Implements PSO Vi+1 update algorithm where:
		// Vi+1 = a*Vi + u1*(Xi - Pi) + u2*(Xi - Ni)
		velocity = piecewise_addition(piecewise_addition(scalar_mult(inertia, velocity), // Maintain current velocity
			piecewise_multiplication(u1, piecewise_subtraction(location, p_best))), // bias towards p_best
			piecewise_multiplication(u2, piecewise_subtraction(location, g_best))); // bias towards n_best
	}

	/**
	* Given velocity and current location, update location
	*/
	public void update_location() {
		location = piecewise_addition(location, velocity);
	}

	public void eval_ackley(int dimensions) {
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

	public void eval_rosenbrock(int dimensions) {
		double sum = 0;
		for (int i = 0; i < dimensions - 1; i ++) {
			sum += 100*(location[i+1] - Math.pow(location[i], 2) + Math.pow(location[i] - 1, 2));
		}
		return sum;
	}

	public void eval_rastrigin(int dimensions) {
		sum = 0;
		for (i = 0; i < dimensions; i ++) {
			sum += Math.pow(location[i], 2) - 10*Math.cos(2*Math.PI*location[i]) + 10;
		}
	}

}