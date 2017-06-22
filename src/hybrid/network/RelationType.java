package hybrid.network;
/**
 * Represents the relation type:
 * 1) Internal - relationships between objects of the same class e.g., friend(student,student)
 * 2) External - relationships between objects of different types, grade(student,course)
 * @author irma
 *
 */
public enum RelationType {
      EXTERNAL, INTERNAL;
}
