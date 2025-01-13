/**
 * The {@code manager} class represents a manager in the system.
 * It extends the {@code user} class, inheriting its properties and behavior.
 */
class manager extends user{

    /**
     * Constructs a new {@code manager} object with the specified name, surname, username, and role.
     *
     * @param name     The first name of the manager.
     * @param surname  The last name of the manager.
     * @param username The username of the manager.
     * @param role     The role of the manager in the system.
     */
    manager(String name, String surname, String username, String role) {
        super(name, surname, username, role);
    }
}
