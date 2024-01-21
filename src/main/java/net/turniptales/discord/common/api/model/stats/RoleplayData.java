package net.turniptales.discord.common.api.model.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class RoleplayData {

    private long id;
    private String firstname;
    private String lastname;
    private Gender gender;
    private long birthday;
    private double bankBalance;
    private double cashBalance;
    private double payPalBalance;
    private double wantedPercentage;
    private double hydration;

    public String getRoleplayDisplayName() {
        return this.firstname + " " + this.lastname;
    }

    @Getter
    @AllArgsConstructor
    public enum Gender {

        MAN("Männlich", "Herr", "Vater", "Sohn", "Bruder"),
        FEMALE("Weiblich", "Frau", "Mutter", "Tochter", "Schwester");

        private final String adjective;
        private final String salutation;
        private final String parent;
        private final String children;
        private final String sibling;

        public String getUnknownName() {
            return this.equals(MAN) ? "Fremder" : "Fremde";
        }
    }
}
