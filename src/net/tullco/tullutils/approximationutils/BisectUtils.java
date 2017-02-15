package net.tullco.tullutils.approximationutils;

import net.tullco.tullutils.exceptions.UnapproximatableException;


public class BisectUtils {
	private static int MAX_RECURSION_DEPTH = 500;
	/**
	 * Recursively approximates an x value that will equal goal when put through the given function.
	 * Only works for functions that have strict positive or negative relationships.
	 * Function must increase or decrease reliably with it's argument. Otherwise, you're likely to get unexpected results. 
	 * @param goal The value you want the function to have for the x value you are searching for.
	 * @param initial The value you want the approximation to start from.
	 * @param startStep The original step size you want to take. Will also define the size of your bisect range and how much seeking will be done. 
	 * @param precision How close your final answer needs to be to the goal. Smaller numbers take longer.
	 * @param function The function to be tested. A class or lambda that implements the Approximatable interface. 
	 * @throws UnapproximatableException If, for whatever reason, an approximation with adequate precision cannot be reached, this will be thrown. 
	 * @return The requested root of the approximatable function.
	 */	
	public static double bisectApproximate(
			double goal,
			double initial,
			double startStep,
			double precision,
			Approximatable function)
					throws UnapproximatableException
	{
		double guess = initial;
		double guessResult = function.output(initial);
		
		//We have to determine if the function is increasing or decreasing. Wouldn't want to end up endlessly moving in the wrong direction, now would we? ;) 
		double guessAdditionResult = function.output(initial+1);
		//System.out.println("Original output: "+guessResult+" AdditionResult: "+ guessAdditionResult);
		boolean direction=false;
		if(guessResult<=guessAdditionResult){
			direction=true;
		}
		double distanceToGoal = goal-guessResult;
		
		//If the initial guess was fine... Well, lets just return it. ;)
		if(Math.abs(distanceToGoal)<=precision){
			return guess;
		}
		
		//Alas, we have to do hard work now.... :(
		//We'll find a range to bisect first.
		boolean originalSideOfGoal = distanceToGoal > 0;
		double nextStep;
		if(originalSideOfGoal ^ direction)
			nextStep=-startStep;
		else
			nextStep=startStep;
		boolean currentSideOfGoal = originalSideOfGoal;
		double lastGuess=guess;
		//Try this until you manage to cross over the goal.
		while(!(currentSideOfGoal ^ originalSideOfGoal)){
			//System.out.println(guess);
			lastGuess=guess;
			guess=lastGuess+nextStep;
			guessResult = function.output(guess);
			distanceToGoal=goal-guessResult;
			currentSideOfGoal = distanceToGoal > 0;
		}
		
		//Alright... Lets store the range we found.
		double upperBound,lowerBound;
		if(lastGuess<guess){
			upperBound=guess;
			lowerBound=lastGuess;
		}else{
			upperBound=lastGuess;
			lowerBound=guess;
		}
		
		//Now, we start the bisecting. :)
		
		double answer = bisectApproximator(upperBound,lowerBound,goal,precision,direction,0,function);
		//And return the answer.
		return answer;
	}
	
	private static double bisectApproximator(double upperBound, double lowerBound, double goal, double precision,boolean direction ,int depth,Approximatable function) throws UnapproximatableException{
		//System.out.println(String.format(Locale.US,"Attempt: %d Upper bound: %2$,.2f Lower Bound: %3$,.2f", depth, upperBound,lowerBound));
		//Don't go any deeper, Leo! O:)
		if(depth > MAX_RECURSION_DEPTH)
			throw new UnapproximatableException();
		
		double pivot = (upperBound+lowerBound)/2;
		double pivotResult = function.output(pivot);
		if(Math.abs(goal-pivotResult) < precision)
			return pivot;
		if(!(pivotResult<goal ^ direction )){
			return bisectApproximator(upperBound,pivot,goal,precision,direction,depth+1,function);
		}
		else{
			return bisectApproximator(pivot,lowerBound,goal,precision,direction,depth+1,function);
		}
	}
}
