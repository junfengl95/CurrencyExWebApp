package com.fdm.velocitytrade;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component
public final class Global {

	// round to 5 decimal places for intermediate operations
//	public static final String ROUNDING = "%.10f";

	// Currently undocumented on the value chosen
	private int sigFig = 10;

	// fix round to 10s.f.
	public double round(double input) {

		MathContext Context = new MathContext(this.sigFig, RoundingMode.HALF_DOWN);
		System.out.println("DEBUG LOG For global::round : " + input + ",Rounded value is : "
				+ new BigDecimal(input).round(Context).doubleValue());
		return new BigDecimal(input).round(Context).doubleValue();

	}

	// specify rounding
	public double round(double input, int sigFig) {

		MathContext Context = new MathContext(sigFig, RoundingMode.HALF_DOWN);
		System.out.println("DEBUG LOG For global::round, input : " + input + ", Rounded value is : "
				+ new BigDecimal(input).round(Context).doubleValue());
		return new BigDecimal(input).round(Context).doubleValue();

	}

	// method checks if trade is within or close to zero by a factor of 10^-6
	// If the value is less than 1 millionth, can consider it to be zero
	public double isZero(double input) {

		int decimalPlaces = 6;
		double output = (Math.abs(input) <= Math.pow(10, -6)) ? 0 : input;
		System.out.println("DEBUG LOG For global::isZero, input is : " + input + ", with lower limit as "
				+ Math.pow(10, -(decimalPlaces)) + ", Output is : " + output);
		return output;

	}

}
