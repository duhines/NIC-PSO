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

	public void update_velocity(g_best) {

	}

	public void update_location() {

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
			sum += Math.pow(location[i], 2) - 10*Math.cos(2*Math.PI*location[i]) + 10
		}
	}
}