import java.util.Scanner;

public class phase1 {
    static class trieTree {
        class Node {//ravesh jadid trie tree(farz mikonim 30 sfr vojod darad ke moadel adad 0 hast hala har marhale do halat darad ke 0 va 1 ast
            Node zeroChild = null;//farzand zero
            Node oneChild = null;//farzand yek
        }

        private Node root = new Node();//rishe ke barabar ba sfr ast

        void insert(String key) {
            String number = strToBinary(Integer.parseInt(String.valueOf(key)));//tabdil be binary krdn
            Node temp = root;
            for (int i = 0; i < number.length(); i++) {
                int t = Integer.parseInt(String.valueOf(number.charAt(i)));
                if (t == 0) {//agar bit sefr bashad
                    if (temp.zeroChild == null) {//agar zero ejad nashode bood an ra misazim vagar na mirim zero
                        temp.zeroChild = new Node();
                        temp = temp.zeroChild;
                    } else {
                        temp = temp.zeroChild;
                    }
                } else {//agar bit yek bashad
                    if (temp.oneChild == null) {//agar yek ejad nashode bood an ra misazim vagar na mirim yek
                        temp.oneChild = new Node();
                        temp = temp.oneChild;
                    } else {
                        temp = temp.oneChild;
                    }
                }
                /////end of changed but not necessary /////
            }

        }

        String search(String key) {
            String number = strToBinary(Integer.parseInt(String.valueOf(key)));//tabdil be binary krdn
            Node temp = root;
            StringBuilder output = new StringBuilder();//khoroji mored nazar
            for (int i = 0; i < number.length(); i++) {
                int t = Integer.parseInt(String.valueOf(number.charAt(i)));//bit aval adad vorodi
                if (t == 0 && temp.oneChild != null) {//agar bit adad vorodi 0 bod dar har marhale bayad berim farazande 1
                    output.append("1");
                    temp = temp.oneChild;
                } else if (t == 0) {
                    output.append("0");
                    temp = temp.zeroChild;
                } else if (t == 1 && temp.zeroChild != null) {//agar bit adad vorodi 1 bod dar har marhale bayad berim farazande 0
                    output.append("1");
                    temp = temp.zeroChild;
                } else if (t == 1) {
                    output.append("0");
                    temp = temp.oneChild;
                } else {
                    output = new StringBuilder("0");
                }
            }
            return output.toString();
        }

        private String strToBinary(int number) {//tabdil adad be binary
            int[] bNum = new int[30];
            int i = 0;
            while (number > 0) {
                bNum[i] = number % 2;
                number = number / 2;
                i++;
            }
            StringBuilder output = new StringBuilder();
            for (int j = 29; j >= 0; j--) {
                output.append(bNum[j]);
            }
            return output.toString();
        }

    }

    private static trieTree t = new trieTree();

    public static void main(String[] args) {
        t.insert("0");
        Scanner in = new Scanner(System.in);
        int q = Integer.parseInt(in.nextLine());
        String[] num = new String[q];//tamam khoroji ha
        String input;
        for (int i = 0; i < q; i++) {//khandan khat be khat
            input = in.nextLine();
            num[i] = operator(input);
        }
        String output = strToDecimal(num);
        System.out.print(output);
    }

    private static String strToDecimal(String[] str) {//tabdil khoroji ha be decimal
        StringBuilder output = new StringBuilder();
        for (String s : str) {
            if (s != null) {
                int sum = 0;
                String temp;
                for (int i = s.length() - 1; i >= 0; i--) {
                    temp = String.valueOf(s.charAt(i));
                    if (temp.equals("1")) {
                        sum = sum + (int) Math.pow(2, s.length() - 1 - i);
                    }
                }
                output.append(sum).append("\n");
            }
        }
        return output.toString();
    }

    private static String operator(String input) {
        String[] in = input.split(" ");
        String output = null;
        switch (in[0]) {
            case "1":
                t.insert(in[1]);
                break;
            case "2":
                output = t.search(in[1]);
                break;
            default:
                break;
        }
        return output;
    }
}
/*
5
2 1
1 1
1 2
1 3
2 0
 */