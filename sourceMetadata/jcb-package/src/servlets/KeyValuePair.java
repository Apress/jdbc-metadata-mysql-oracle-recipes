/**
 * Date: November 5, 2000
 * <p>
 * @author Mahmoud Parsian
 * @since 1.3
 * <p>
 *
 *  Purpose:
 *    This class is a helper class for constructing HTML options.
 *
 *
 */
public class KeyValuePair {

    private String key;
    private String value;

    public KeyValuePair(String k, String v) {
		key = k;
		value = v;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getHTMLOption() {
		return "<option value=\""+key+"\">"+value+"</option>";
	}

	public String getHTMLOption(String selectedKey) {

			if (key.equals(selectedKey)) {
				return "<option value=\""+key+"\" selected>"+value+"</option>";
			}
			else {
				return "<option value=\""+key+"\">"+value+"</option>";
			}
	}
}

