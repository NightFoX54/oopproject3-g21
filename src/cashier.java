/**
 * The {@code cashier} class represents a cashier in the system.
 * It extends the {@code user} class, inheriting its properties and behavior.
 */
class cashier extends user{

    /**
     * Constructs a new {@code cashier} object with the specified name, surname, username, and role.
     *
     * @param name     The first name of the cashier.
     * @param surname  The last name of the cashier.
     * @param username The username of the cashier.
     * @param role     The role of the cashier in the system.
     */
    cashier(String name, String surname, String username, String role) {
        super(name, surname, username, role);
    }
}
