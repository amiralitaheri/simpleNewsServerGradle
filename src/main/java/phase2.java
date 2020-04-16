import java.util.Scanner;

public class phase2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] input = new int[n];
        int xor = 0;
        int[] forward = new int[n];
        for (int i = 0; i < n; i++) {
            input[i] = scanner.nextInt();
            xor ^= input[i];
            forward[i] = xor;
        }
        xor = 0;
        int[] backward = new int[n];
        int count = 0;
        for (int i = n - 1; i >= 0; i--) {
            xor ^= input[i];
            backward[count++] = xor;
        }
        int max = -1;
        for (int i = 0; i < n; i++) {
            max = (forward[i] > max) ? forward[i] : max;
            max = (backward[i] > max) ? backward[i] : max;

        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i + j > n - 2) {
                    break;
                }
                max = ((forward[i] ^ backward[j]) > max) ? (forward[i] ^ backward[j]) : max;
            }
        }
        System.out.println(max);

    }
}
