abstract class user {
    protected String name;
    protected String surname;
    protected String username;
    protected String role;

    public user(String name, String surname, String username, String role) {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.role = role;
    }
        // Getter methods
        public String getName() {
            return name;
        }
    
        public String getSurname() {
            return surname;
        }
    
        public String getUsername() {
            return username;
        }
    
        public String getRole() {
            return role;
        }

}

