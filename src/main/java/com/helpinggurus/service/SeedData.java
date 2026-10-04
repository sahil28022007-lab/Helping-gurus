package com.helpinggurus.service;

import com.helpinggurus.dao.*;
import com.helpinggurus.exception.HelpingGurusException;
import com.helpinggurus.model.*;
import com.helpinggurus.util.PasswordUtil;

/** Loads fictional demo data on the very first start. All names, stories and amounts are made up. */
public final class SeedData {
    private SeedData() {}

    /** Inserts demo users, campaigns, updates and donations, but only when the users table is empty. */
    public static void runIfEmpty() throws HelpingGurusException {
        UserDao users = new UserDao();
        if (users.count() > 0) return;
        User admin = users.save(new Admin(0, "Admin Guru", "admin@helpinggurus.org", PasswordUtil.hash("admin123")));
        User priya = users.save(new Creator(0, "Priya Sharma", "priya@mail.com", PasswordUtil.hash("priya123")));
        User kabir = users.save(new Creator(0, "Dr. Kabir Rao", "kabir@mail.com", PasswordUtil.hash("kabir123")));
        User donor = users.save(new Contributor(0, "Guest Donor", "donor@mail.com", PasswordUtil.hash("donor123")));

        CampaignDao cd = new CampaignDao();
        Campaign a = live(cd, "Aarav's fight against SMA", "Zolgensma - SMA", priya, 160_000_000, 98_500_000,
            "Aarav is 14 months old and living with Spinal Muscular Atrophy Type 1. A one-time gene therapy, Zolgensma, can give him a chance to sit, stand and grow.");
        cd.addTeamMember(a.getId(), kabir.getId());
        new PostDao().add(new Post(a.getId(), "Priya Sharma", "UPDATE", "Doctors confirmed Aarav is eligible for the therapy. Thank you all!"));
        live(cd, "Myra will walk: Zolgensma mission", "Zolgensma - SMA", kabir, 160_000_000, 41_200_000,
            "Myra loves to laugh, but SMA is weakening her muscles every month. Her parents are racing to fund the treatment.");
        live(cd, "Ananya vs Leukemia", "Cancer Fighter", priya, 2_500_000, 1_420_000,
            "Ananya, 9, is in her second phase of chemotherapy for acute lymphoblastic leukemia and dreams of going back to school.");
        live(cd, "Meera Devi: breast cancer warrior", "Cancer Fighter", kabir, 1_800_000, 730_000,
            "Meera taught for 25 years. Now she is fighting Stage 2 breast cancer and her community is standing by her.");
        Campaign r = new Campaign("Rohan's neuroblastoma treatment", "Cancer Fighter",
            "Rohan, 5, needs immunotherapy after a neuroblastoma diagnosis. Awaiting verification.", 3_200_000, priya.getId());
        r.setChkId(true);
        cd.save(r);

        DonationService ds = new DonationService();
        ds.donate(donor, a.getId(), 5000, "Stay strong little champ!", false);
        ds.donate(donor, a.getId(), 1000, "", true);
    }

    /** Helper: creates a fully verified, LIVE campaign with some money already raised. */
    private static Campaign live(CampaignDao cd, String t, String cat, User owner, double goal, double raised, String story) throws HelpingGurusException {
        Campaign c = new Campaign(t, cat, story, goal, owner.getId());
        c.setRaised(raised); c.setChkId(true); c.setChkMedical(true); c.setChkHospital(true); c.setChkBank(true);
        c.setStatus(Campaign.Status.LIVE);
        return cd.save(c);
    }
}
