/**
 * The {@code admin} class represents an administrator user in the system.
 * It extends the {@code user} class and inherits its properties and methods.
 */
class admin extends user{

     /**
     * Constructs an {@code admin} object with the specified name, surname, username, and role.
     *
     * @param name The name of the admin.
     * @param surname The surname of the admin.
     * @param username The username of the admin.
     * @param role The role of the admin.
     */
    admin(String name, String surname, String username, String role) {
        super(name, surname, username, role);
    }
}
