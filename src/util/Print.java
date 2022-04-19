package util;

public class Print {
	private static String prefix = "[MorpionCrypto]";

	/**
	 * Affiche un message de type 'réussite'
	 */
	public static String success(String message) {
		return SystemPrint(prefix, message, "(Succès)");
	}

	/**
	 * Affiche un message de type 'erreur'
	 */
	public static String error(String message) {
		return SystemPrint(prefix, message, "(Erreur) =(!)=");
	}

	/**
	 * Affiche un message de type 'alert'
	 */
	public static String alert(String message) {
		return SystemPrint(prefix, message, "");
	}
	
	/**
	 * Affiche un message de type 'test'
	 */
	public static String test(String message) {
		return SystemPrint("--Test--", message, "");
	}
	
	/**
	 * Affiche un message
	 */
	private static String SystemPrint(String prefix, String message, String suffix) {
		String value = prefix + " " + message;
		if (suffix.length() > 0)
			value += " " + suffix;
		System.out.println(value);
		return value;
	}
}
