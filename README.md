# NIC-PSO
Project 2 for Nature Inspire Computation Fall 2018
 
This project implements a PSO algorithm with 4 different neighborhood topologies and 
3 benchmark functions for testing.  

To run the algorithm, first compile the java class files.
Then, run the PSO class file with the following arguments:
	java PSO <swarm size> <number of iterations> <benchmark> <topology>
	
	Swarm size: integer values > 0
	Number of iterations: integer value > 0
	Benchmark: ack for Ackley, ras for Rastrigin, rok for Rosenbrock
	Topology: gl for global, ri for ring, vn for von Neumann, ra for random
	
Output will be printed to I/O.  