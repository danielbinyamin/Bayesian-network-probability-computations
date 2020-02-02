import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * This class represents an answer for a given query.
 * An answer hold:
 * -the result rounded to five after decimal point.
 * -the number of sums.
 * -the number of products
 * @author daniel.
 *
 */
public class Answer {

	private double _ans;
	private int _numOfSums;
	private int _numOfProducts;

	public Answer(double ans, int numOfSums, int numOfProducts) {
		this._ans = ans;
		_ans = roundToFive(_ans);
		this._numOfSums = numOfSums;
		this._numOfProducts = numOfProducts;

	}

	private double roundToFive(double num) {
		BigDecimal bd = new BigDecimal(Double.toString(num));
		bd = bd.setScale(5, RoundingMode.HALF_UP);
		num = bd.doubleValue();
		return bd.doubleValue();
	}

	@Override
	public String toString() {
		String text = Double.toString(Math.abs(_ans));
		int integerPlaces = text.indexOf('.');
		int decimalPlaces = text.length() - integerPlaces - 1;
		if(decimalPlaces<5) {
			DecimalFormat formatter = new DecimalFormat("0.00000");
			String string_ans = formatter.format(_ans);
			return string_ans + "," + _numOfSums + "," + _numOfProducts;
		}

		return _ans + "," + _numOfSums + "," + _numOfProducts;
	}




}
