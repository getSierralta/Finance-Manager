package pt.upskill.projeto2.financemanager.categories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author upSkill 2020
 * <p>
 * ...
 */

public class Category implements Serializable{

    private String name;
    private List<String> tags= new ArrayList<>();
    private static final long serialVersionUID = -9107819223195202547L;

    public Category(String string) {
        this.name = string;
    }

    /**
     * Função que lê o ficheiro categories.txt e gera uma lista de {@link Category} (método fábrica)
     * Deve ser utilizada a desserialização de objetos para ler o ficheiro binário categories.txt.
     *
     * @param file - Ficheiro onde estão apontadas as categorias possíveis iniciais, numa lista serializada (por defeito: /account_info/categories.txt)
     * @return uma lista de categorias, geradas ao ler o ficheiro
     */
    public static List<Category> readCategories(File file){
        List<Category> categories = new ArrayList<>();
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            categories = (List<Category>) in.readObject();
            in.close();
            fileIn.close();
            return categories;
        }catch (FileNotFoundException e){
            System.out.println("Error reading the file of categories");
        }catch (IOException e) {
            System.out.println("They arent any categories");
        } catch (ClassNotFoundException e) {
            System.out.println("Couldn't convert the categories.txt!");
        }
        return categories;
    }

    /**
     * Função que grava no ficheiro categories.txt (por defeito: /account_info/categories.txt) a lista de {@link Category} passada como segundo argumento
     * Deve ser utilizada a serialização dos objetos para gravar o ficheiro binário categories.txt.

     * @param categories the list with the categories.txt
     */
    public static void writeCategories(List<Category> categories) {
        try {
            FileOutputStream fileOut = new FileOutputStream(new File("account_info/categories"));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(categories);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            System.out.println("Error writing the categories");
        }

    }

    public boolean hasTag(String tag) {
        if (tags.size() > 0) {
            for (String tag1 : tags) {
                if (tag1.equals(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String tag1 : tags) {
            stringBuilder.append(tag1).append("\t");
        }
        return "Category{" +
                "name='" + name + '\'' +
                ", tags=" + stringBuilder+
                '}';
    }
}
