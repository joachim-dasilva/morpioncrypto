package morpion;

public class Morpion {
	private int[][] grid = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };

	public Morpion() {
	}

	/**
	 * Récupère la valeur de la case du morpion
	 */
	public int getValueAtIndex(int index) {
		int row = 0;
		while (index - 3 > 0) {
			row++;
			index = index - 3;
		}
		return grid[row][index - 1];
	}

	/*
	 * Placer le jeton d'un joueur sur le morpion
	 */
	public void placeToken(int a, int player) {
		int row = 0;
		while (a - 3 > 0) {
			row++;
			a = a - 3;
		}
		grid[row][a - 1] = player;
	}

	/*
	 * Vérification de l'état du morpion
	 */
	public int winConditions() {
		int token;
		boolean wrong;
		// Lignes
		for (int row = 0; row < grid.length; row++) {
			token = grid[row][0];
			wrong = false;
			for (int col = 0; !wrong && col < grid[0].length; col++) {
				if (grid[row][col] == 0 || grid[row][col] != token)
					wrong = true;
			}
			if (!wrong)
				return token;
		}
		// Colonnes
		for (int col = 0; col < grid[0].length; col++) {
			token = grid[0][col];
			wrong = false;
			for (int row = 0; !wrong && row < grid.length; row++) {
				if (grid[row][col] == 0 || grid[row][col] != token)
					wrong = true;
			}
			if (!wrong)
				return token;
		}
		// Diagonale 1
		token = grid[0][0];
		wrong = false;
		for (int i = 0; !wrong && i < grid.length; i++) {
			if (grid[i][i] == 0 || grid[i][i] != token)
				wrong = true;
		}
		if (!wrong)
			return token;

		// Diagonale 2
		token = grid[grid.length - 1][0];
		wrong = false;
		for (int i = 0; !wrong && i < grid.length; i++) {
			if (grid[grid.length - 1 - i][i] == 0 || grid[grid.length - 1 - i][i] != token)
				wrong = true;
		}
		if (!wrong)
			return token;
		return 0;
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				char value = ' ';
				if (grid[i][j] == 1)
					value = 'O';
				else if (grid[i][j] == 2)
					value = 'X';
				result += " " + value + " " + "|";
			}
			result = result.substring(0, result.length() - 2);
			result += "\n--- --- ---\n";
		}
		result = result.substring(0, result.length() - 12);
		return result;
	}
}
