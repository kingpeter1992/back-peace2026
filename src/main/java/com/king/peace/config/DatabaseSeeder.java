package com.king.peace.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.king.peace.Dao.CaisseSessionRepository;
import com.king.peace.Dao.ClientRepository;
import com.king.peace.Dao.ContratRepository;
import com.king.peace.Dao.GardienRepository;
import com.king.peace.Dao.RoleRepository;
import com.king.peace.Dao.TransactionCaisseRepository;
import com.king.peace.Dao.UserRepository;
import com.king.peace.Entitys.CaisseSession;
import com.king.peace.Entitys.CategorieOperation;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Contrats;
import com.king.peace.Entitys.Devise;
import com.king.peace.Entitys.ERole;
import com.king.peace.Entitys.Gardien;
import com.king.peace.Entitys.ModePaiement;
import com.king.peace.Entitys.Role;
import com.king.peace.Entitys.StatutGardien;
import com.king.peace.Entitys.StatutSessionCaisse;
import com.king.peace.Entitys.TransactionCaisse;
import com.king.peace.Entitys.TypeTransaction;
import com.king.peace.Entitys.User;
import com.king.peace.enums.Fonction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Profile("dev") // ✅ SEULEMENT quand spring.profiles.active=dev
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final GardienRepository gardienRepository;
    private final ClientRepository clientRepository;
    private final ContratRepository contratsRepository;
    private final PasswordEncoder encoder;
    private final CaisseSessionRepository caisseSessionRepository;
    private final TransactionCaisseRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("🚀 DatabaseSeeder (PROFILE=dev) démarrage...");

        // 0) Roles
        createRoleIfNotExists(ERole.ROLE_ADMIN);
        createRoleIfNotExists(ERole.ROLE_CAISSIER);
        createRoleIfNotExists(ERole.ROLE_RESPONSABLE_PERSONNEL);
        createRoleIfNotExists(ERole.ROLE_USER);

        // 1) Admin
        seedAdmin();

        // 2) Gardiens
        if (gardienRepository.count() == 0) seedGardiens();
        else System.out.println("ℹ️ Seed gardiens ignoré: déjà existants.");

        // 3) Clients (ID 5 chiffres via sequence)
        List<Client> clients;
        if (clientRepository.count() == 0) clients = seedClients();
        else {
            System.out.println("ℹ️ Seed clients ignoré: déjà existants.");
            clients = clientRepository.findAll();
        }

        // 4) Contrats
        if (contratsRepository.count() == 0) seedContrats(clients);
        else System.out.println("ℹ️ Seed contrats ignoré: déjà existants.");

        // 5) Transactions
        if (transactionRepository.count() == 0) seedTransactions();
        else System.out.println("ℹ️ Seed transactions ignoré: déjà existantes.");

        System.out.println("🎉 DatabaseSeeder terminé.");
    }

    // -------------------------
    // Roles
    // -------------------------
    private void createRoleIfNotExists(ERole roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            System.out.println("✔ Role créé : " + roleName);
        }
    }

    // -------------------------
    // Admin
    // -------------------------
    private void seedAdmin() {
        System.out.println("🚀 Initialisation utilisateur admin...");
        if (userRepository.findByEmail("kingkapeta@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("kingkapeta@gmail.com");
            admin.setPassword(encoder.encode("123456789"));
            admin.setActive(true);

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));

            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);

            System.out.println("✅ Utilisateur ADMIN créé !");
        } else {
            System.out.println("ℹ️ Admin déjà existant.");
        }
    }

    // -------------------------
    // Seed Gardiens
    // -------------------------
    private void seedGardiens() {
        String[] nomsG = {
                "AMISI","KABONGO","MUKENDI","KASONGO","ILUNGA",
                "MUTOMBO","KAPETA","MWAMBA","TSHIBANGU","KALALA",
                "MULUMBA","BAYONGA","MULENGA","KATUMBA","LUKUSA",
                "MUTALE","BAMBA","KAYEMBE","MUSHI","BONGELI"
        };

        String[] prenomsG = {
                "Jean","Patrick","David","Samuel","Christian",
                "Grace","Esther","Rachel","Daniel","Michel",
                "Jonathan","Eric","Cedric","Benjamin","Moise",
                "Innocent","Gloria","Fabrice","Joel","Nathan"
        };

        List<Gardien> gardiens = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Gardien g = new Gardien();

            g.setNom(nomsG[i]);
            g.setPrenom(prenomsG[i]);

            g.setTelephone1("08" + (1 + (i % 3)) + (2000000 + i * 123));
            g.setTelephone2("09" + 9 + (3000000 + i * 321));

            g.setFonction(Fonction.AGENT);

            Devise deviseG = (i % 2 == 0) ? Devise.USD : Devise.CDF;
            g.setDevise(deviseG);

            if (deviseG == Devise.USD) {
                g.setSalaireBase(250 + (i * 5));
                g.setSalaire(250 + (i * 5));
            } else {
                g.setSalaireBase(600000 + (i * 10000));
                g.setSalaire(600000 + (i * 10000));
            }

            g.setAdresse("Lubumbashi - Kenya N°" + (10 + i));
            g.setGenre(i % 2 == 0 ? "M" : "F");
            g.setStatut(StatutGardien.ACTIF);
            g.setDateEmbauche(LocalDate.now().minusMonths(6L + i * 2L));
            g.setEmail(prenomsG[i].toLowerCase() + "." + nomsG[i].toLowerCase() + "@peace-security.cd");
            g.setDateNaissance(LocalDate.of(1986 + (i % 12), 1 + (i % 12), 5 + (i % 20)));
            g.setCreatedAt(LocalDate.now());
            g.setActif(true);

            gardiens.add(g);
        }

        gardienRepository.saveAll(gardiens);
        System.out.println("✅ 20 gardiens créés.");
    }

    // -------------------------
    // Seed Clients (ID 5 chiffres)
    // -------------------------
    private List<Client> seedClients() {
        String[] nomsC = {
                "HÔTEL KARAVIA", "ECOLE LA REFERENCE", "CLINIQUE MWANA",
                "SUPERMARCHE KENYA", "IMMEUBLE MUKUBA", "BANQUE TRUST RDC",
                "DEPOT MINIER KCC", "STATION TOTAL LUB", "ENTREPRISE KAPETA SARL",
                "MARCHE CENTRAL", "HOPITAL SENDWE", "UNIVERSITE DE LUBUMBASHI",
                "USINE TEXAF", "ECOLE SAINT PAUL", "CENTRE COMMERCIAL GALAXY",
                "RESIDENCE ILUNGA", "IMMEUBLE KASAPA", "MAGASIN KABONGO",
                "BUREAU MUTOMBO", "DEPOT MWAMBA"
        };

        String[] typesClient = {"Commercial", "Residentiel", "Industriel"};

        List<Client> clients = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Long next = clientRepository.nextClientId();
            if (next == null) throw new RuntimeException("Séquence client_seq introuvable / nextClientId() null");
            if (next > 99999) throw new RuntimeException("Limite 5 chiffres atteinte (client_seq > 99999)");

            Client c = new Client();
            c.setId(next);

            c.setNom(nomsC[i]);
            c.setAdresse("Lubumbashi - Commune " + (1 + (i % 7)));
            c.setContact("08" + (1 + (i % 3)) + (3000000 + i * 147));
            c.setContact2("09" + 9 + (2000000 + i * 258));
            c.setEmail("contact" + (i + 1) + "@peace-client.cd");
            c.setTypeClient(typesClient[i % typesClient.length]);
            c.setActif(true);

            clients.add(c);
        }

        List<Client> saved = clientRepository.saveAll(clients);
        System.out.println("✅ 20 clients créés (ID 5 chiffres).");
        return saved;
    }

    // -------------------------
    // Seed Contrats
    // -------------------------
    private void seedContrats(List<Client> clients) {
        String[] zones = {"Kenya", "Golf", "Bel-Air", "Kamalondo", "Kiwele", "Kasapa", "Ruashi"};
        String[] activites = {"Hôtel", "École", "Clinique", "Supermarché", "Immeuble", "Banque", "Dépôt", "Station-service"};
        String[] services = {"Surveillance jour", "Surveillance nuit", "Surveillance 24/24", "Contrôle accès", "Patrouille"};
        String[] paiements = {"mensuel", "hebdomadaire", "fin_service"};

        List<Contrats> contrats = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < clients.size(); i++) {
            Client c = clients.get(i);

            Contrats ctr = new Contrats();
            ctr.setClient(c);

            String ref;
            do {
                ref = "CTR-" + (100000 + r.nextInt(900000));
            } while (contratsRepository.existsByRefContrats(ref));
            ctr.setRefContrats(ref);

            LocalDate debut = LocalDate.now().minusDays(5L + i);
            LocalDate fin = debut.plusMonths(12);

            ctr.setDateDebut(debut);
            ctr.setDateFin(fin);
            ctr.setDateDebutFacturation(debut.plusDays(1));
            ctr.setDateEmission(LocalDate.now());

            ctr.setZone(zones[i % zones.length]);
            ctr.setActiviteClient(activites[i % activites.length]);
            ctr.setTypeService(services[i % services.length]);
            ctr.setTypePaiement(paiements[i % paiements.length]);

            ctr.setFrequence(30);
            ctr.setNombreJoursMensuel(30);

            int nbG = 1 + (i % 3);
            ctr.setNombreGardiens(nbG);

            Devise deviseCtr = (i % 2 == 0) ? Devise.USD : Devise.CDF;
            ctr.setDevise(deviseCtr);

            double montantParGardien = (deviseCtr == Devise.USD)
                    ? (250 + (i * 3))
                    : (600000 + (i * 8000));

            ctr.setMontantParGardien(montantParGardien);
            ctr.setMontant(nbG * montantParGardien);

            ctr.setDescription("Contrat auto seed - " + c.getNom() + " (" + ctr.getZone() + ")");
            ctr.setStatut("EMIS");
            ctr.setActive(true);

            contrats.add(ctr);
        }

        contratsRepository.saveAll(contrats);
        System.out.println("✅ 20 contrats créés.");
    }

    // -------------------------
    // Seed Transactions
    // -------------------------
    private void seedTransactions() {

        CaisseSession session = caisseSessionRepository
                .findByDateJourAndStatut(LocalDate.now(), StatutSessionCaisse.OUVERTE)
                .orElseGet(() -> {
                    CaisseSession s = new CaisseSession();
                    s.setDateJour(LocalDate.now());
                    s.setStatut(StatutSessionCaisse.OUVERTE);
                    s.setSoldeActuelUSD(500.0);
                    s.setSoldeActuelCDF(2_000_000.0);
                    return caisseSessionRepository.save(s);
                });

        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            System.out.println("⚠️ Aucun client: seed transactions annulé.");
            return;
        }

        Random r = new Random();
        int totalTx = 50;

        for (int i = 1; i <= totalTx; i++) {

            Devise devise = (i % 2 == 0) ? Devise.USD : Devise.CDF;

            TypeTransaction type = (r.nextInt(10) < 7)
                    ? TypeTransaction.ENCAISSEMENT
                    : TypeTransaction.DECAISSEMENT;

            double soldeAvant = (devise == Devise.USD)
                    ? session.getSoldeActuelUSD()
                    : session.getSoldeActuelCDF();

            double montant = (devise == Devise.USD)
                    ? (20 + r.nextInt(200))
                    : (50_000 + r.nextInt(500_000));

            if (type == TypeTransaction.DECAISSEMENT && montant > soldeAvant) {
                type = TypeTransaction.ENCAISSEMENT;
            }

            double soldeApres = (type == TypeTransaction.ENCAISSEMENT)
                    ? soldeAvant + montant
                    : soldeAvant - montant;

            if (devise == Devise.USD) session.setSoldeActuelUSD(soldeApres);
            else session.setSoldeActuelCDF(soldeApres);

            Client client = clients.get(r.nextInt(clients.size()));

            TransactionCaisse tx = new TransactionCaisse();
            tx.setSession(session);
            tx.setDateTransaction(LocalDateTime.now().minusDays(r.nextInt(20)));
            tx.setUserId("seed");
            tx.setType(type);
            tx.setDevise(devise);
            tx.setMontant(montant);
            tx.setModePaiement(ModePaiement.MOBILE_MONEY);
            tx.setCategory(CategorieOperation.AUTRE);
            tx.setClient(client);

            tx.setReference((type == TypeTransaction.ENCAISSEMENT ? "RCPT-" : "PAY-")
                    + System.currentTimeMillis() + "-" + i);

            tx.setDescription((type == TypeTransaction.ENCAISSEMENT ? "Encaissement test " : "Décaissement test ") + i);
            tx.setSoldeAvant(soldeAvant);
            tx.setSoldeApres(soldeApres);
            tx.setSens(type == TypeTransaction.ENCAISSEMENT ? "+" : "-");

            transactionRepository.save(tx);
        }

        caisseSessionRepository.save(session);

        System.out.println("✅ " + totalTx + " transactions seed créées. Solde final USD="
                + session.getSoldeActuelUSD() + " / CDF=" + session.getSoldeActuelCDF());
    }
}