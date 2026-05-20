package sa.edu.kau.fcit.cpit252.project.composite;
import java.util.Set;

// The CatalogComponent interface (product interface).
// This interface has the common attributes that both the tool and tool category share.
public interface CatalogComponent {
    String getName();
    double getPrice();
    String getVariant();
    Set<String> getAllCategories();
}
