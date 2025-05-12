package dtu.timemanager.persistence;

import dtu.timemanager.app.ActivityRepository;
import dtu.timemanager.app.ProjectRepository;
import dtu.timemanager.app.TimeRegistrationRepository;
import dtu.timemanager.app.UserRepository;
import dtu.timemanager.domain.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqliteRepository implements
        ProjectRepository,
        UserRepository,
        ActivityRepository,
        TimeRegistrationRepository {

    private EntityManagerFactory emf;
    private EntityManager em;

    public SqliteRepository() {
        this(false);
    }

    public SqliteRepository(boolean isProduction) {
        Map<String, String> properties = new HashMap<>();

        properties.put("javax.persistence.jdbc.driver", "org.sqlite.JDBC");
        properties.put("javax.persistence.jdbc.url", isProduction ?
                "jdbc:sqlite:lib/db/production.db" :
                "jdbc:sqlite:lib/db/test.db");

        properties.put("eclipselink.ddl-generation", "create-tables");
        properties.put("eclipselink.ddl-generation.output-mode", "database");
        properties.put("eclipselink.target-database", "org.eclipse.persistence.platform.database.SQLitePlatform");

        properties.put("eclipselink.logging.level", "FINE");
        properties.put("eclipselink.logging.timestamp", "true");
        properties.put("eclipselink.logging.thread", "true");
        properties.put("eclipselink.logging.session", "true");
        properties.put("eclipselink.logging.exceptions", "true");

        try {
            emf = Persistence.createEntityManagerFactory("TimeManager", properties);
            em = emf.createEntityManager();
        } catch (PersistenceException e) {
            System.err.println("Persistence initialization error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ProjectRepository Implementation
    @Override
    public Project addProject(String projectName) {
        Project project = new Project(projectName);
        withinTransaction(() -> em.persist(project));
        return project;
    }

    @Override
    public List<Project> getProjects() {
        return em.createQuery("SELECT p FROM Project p", Project.class).getResultList();
    }

    @Override
    public boolean projectExists(Project project) {
        try {
            long count = (long) em.createQuery("SELECT COUNT(p) FROM Project p WHERE p.projectName = :name")
                    .setParameter("name", project.getProjectName())
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public void renameProject(Project project, String newName) {
        withinTransaction(() -> {
            Project managedProject = em.find(Project.class, project.getId());
            if (managedProject != null) {
                managedProject.setProjectName(newName);
                em.merge(managedProject);
            }
        });
    }

    @Override
    public void assignProjectLead(Project project, String userName) {
        withinTransaction(() -> {
            User user = getUserByUsername(userName);
            Project managedProject = em.find(Project.class, project.getId());
            if (managedProject != null && user != null) {
                managedProject.setProjectLead(user);
                em.merge(managedProject);
            }
        });
    }

    // UserRepository Implementation
    @Override
    public void addUser(User user) {
        withinTransaction(() -> em.persist(user));
    }

    @Override
    public List<User> getUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public boolean userExists(String username) {
        try {
            long count = (long) em.createQuery("SELECT COUNT(u) FROM User u WHERE u.userInitials = :username")
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.userInitials = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // ActivityRepository Implementation
    @Override
    public Activity addActivity(Project project, String activityName) throws Exception {
        Activity activity = new Activity(activityName);
        withinTransaction(() -> {
            Project managedProject = em.find(Project.class, project.getId());
            if (managedProject != null) {
                managedProject.addActivity(activity);
                em.merge(managedProject);
            }
        });
        return activity;
    }

    @Override
    public List<Activity> getActivitiesForProject(Project project) {
        Project managedProject = em.find(Project.class, project.getId());
        return managedProject != null ? managedProject.getActivities() : List.of();
    }

    @Override
    public Activity getActivityByName(Project project, String activityName) {
        Project managedProject = em.find(Project.class, project.getId());
        return managedProject != null ? managedProject.getActivityFromName(activityName) : null;
    }

    @Override
    public void setActivityFinalized(Activity activity, boolean finalized) {
        withinTransaction(() -> {
            Activity managedActivity = em.find(Activity.class, activity.getActivityName());
            if (managedActivity != null) {
                if (finalized) {
                    managedActivity.setActivityAsFinalized();
                } else {
                    managedActivity.setActivityAsUnFinalized();
                }
                em.merge(managedActivity);
            }
        });
    }

    @Override
    public void renameActivity(Activity activity, String newName) throws Exception {
        withinTransaction(() -> {
            Project project = em.createQuery(
                            "SELECT p FROM Project p JOIN p.activities a WHERE a = :activity",
                            Project.class)
                    .setParameter("activity", activity)
                    .getSingleResult();
            project.renameActivity(activity, newName);
            em.merge(project);
        });
    }

    // TimeRegistrationRepository Implementation
    @Override
    public TimeRegistration addTimeRegistration(User user, Activity activity, double hours, LocalDate date) throws Exception {
        TimeRegistration registration = new TimeRegistration(user, activity, hours, date);
        withinTransaction(() -> em.persist(registration));
        return registration;
    }

    @Override
    public IntervalTimeRegistration addIntervalTimeRegistration(User user, String leaveOption, LocalDate startDate, LocalDate endDate) throws Exception {
        IntervalTimeRegistration registration = new IntervalTimeRegistration(user, leaveOption, startDate, endDate);
        withinTransaction(() -> em.persist(registration));
        return registration;
    }

    @Override
    public List<TimeRegistration> getTimeRegistrationsForUser(User user) {
        return em.createQuery(
                        "SELECT t FROM TimeRegistration t WHERE t.registeredUser = :user",
                        TimeRegistration.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public List<TimeRegistration> getTimeRegistrationsForActivity(Activity activity) {
        return em.createQuery(
                        "SELECT t FROM TimeRegistration t WHERE t.registeredActivity = :activity",
                        TimeRegistration.class)
                .setParameter("activity", activity)
                .getResultList();
    }

    @Override
    public List<IntervalTimeRegistration> getIntervalTimeRegistrationsForUser(User user) {
        return em.createQuery(
                        "SELECT i FROM IntervalTimeRegistration i WHERE i.registeredUser = :user",
                        IntervalTimeRegistration.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public void updateTimeRegistration(TimeRegistration registration) {
        withinTransaction(() -> em.merge(registration));
    }

    // Helper method for transaction management
    private void withinTransaction(UnitFunction f) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            f.unitFunction();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        }
    }

    // Functional interface for transactions
    interface UnitFunction {
        void unitFunction() throws Exception;
    }

    // Close method
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}