package sa.edu.kau.fcit.cpit252.project;

// The account factory absract class (creator abstract class).
// It defers the instantiation of the account object to its concrete subclasses.

public abstract class AccountFactory {

    // The factory method, which returns an instantiated object that implements
    // the account interface.
    public abstract Account createAccount();
}