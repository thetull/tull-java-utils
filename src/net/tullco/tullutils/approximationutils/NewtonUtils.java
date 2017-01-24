package net.tullco.tullutils.approximationutils;

import net.tullco.tullutils.exceptions.UnapproximatableException;

public class NewtonUtils {
	
	private static int MAX_DEPTH=200;
	
	public static double newtonApproximation(double goal, double startingPoint, double precision, Approximatable a) throws UnapproximatableException{
		double startingOutput = a.output(startingPoint);
		double distanceToGoal = goal - startingOutput;
		//System.out.println("Original distance to goal: "+distanceToGoal);
		if (Math.abs(distanceToGoal) < precision ){
			return startingPoint;
		}
		double slopeAtStart = localDerivative(a, startingPoint, startingOutput);
		double pointSlope=slopeAtStart;
		double value = startingPoint;
		double output = startingOutput;
		int depthCounter=0;
		while (Math.abs(distanceToGoal) > precision){
			/*System.out.println("Value: "+value);
			System.out.println("Slope: "+pointSlope);
			System.out.println("Distance: "+distanceToGoal);*/
			value = (distanceToGoal/pointSlope)+value;
			//System.out.println("New Value: "+value);
			output = a.output(value);
			pointSlope = localDerivative(a, value, output);
			distanceToGoal = goal - output;
			depthCounter++;
			if(depthCounter>MAX_DEPTH)
				throw new UnapproximatableException("Hit max Newton's approximation depth");
		}
		return value;
	}
	private static double localDerivative(Approximatable a, double value, double output){
		double stepSize=0.0001;
		double nearValue = value+stepSize;
		//System.out.println("Near value: "+nearValue);
		double nearOutput = a.output(nearValue);
		//System.out.println("Original output: "+output);
		//System.out.println("Near output: "+nearOutput);
		double valueChange = stepSize;
		double outputChange = nearOutput - output;
		double slope = outputChange/valueChange;
		return slope;
	}
}
