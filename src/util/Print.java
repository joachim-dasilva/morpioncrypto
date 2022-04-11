package util;

public class Print {
	private static String prefix = "[MorpionCrypto]";

	public static String success(String message) {
		return SystemPrint(prefix, message, "(Succès)");
	}

	public static String error(String message) {
		return SystemPrint(prefix, message, "(Erreur) =(!)=");
	}

	public static String alert(String message) {
		return SystemPrint(prefix, message, "");
	}
	
	public static String test(String message) {
		return SystemPrint("--Test--", message, "");
	}
	
	private static String SystemPrint(String prefix, String message, String suffix) {
		String value = prefix + " " + message;
		if (suffix.length() > 0)
			value += " " + suffix;
		System.out.println(value);
		return value;
	}
}
