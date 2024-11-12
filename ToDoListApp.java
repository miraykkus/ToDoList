import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ToDoListApp {

    static class ToDoList {
        private final List<String> tasks = new ArrayList<>();
        private final Scanner scanner = new Scanner(System.in);

        public synchronized void addTask() {
            System.out.print("Yeni görevi girin: ");
            String task = scanner.nextLine();
            if (!task.isEmpty()) {
                tasks.add(task);
                System.out.println("Görev eklendi: " + task);
            } else {
                System.out.println("Görev boş olamaz!");
            }
        }

        public synchronized void listTasks() {
            System.out.println("\n=== Görevler ===");
            if (tasks.isEmpty()) {
                System.out.println("Henüz bir görev yok.");
            } else {
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + ". " + tasks.get(i));
                }
            }
        }

        public synchronized void deleteTask() {
            listTasks();
            if (!tasks.isEmpty()) {
                System.out.print("Silmek istediğiniz görevin numarasını girin: ");
                int taskNumber = scanner.nextInt();
                scanner.nextLine();  

                if (taskNumber > 0 && taskNumber <= tasks.size()) {
                    String removedTask = tasks.remove(taskNumber - 1);
                    System.out.println("\"" + removedTask + "\" görevi silindi.");
                } else {
                    System.out.println("Geçersiz görev numarası!");
                }
            }
        }

        public synchronized List<String> getTasks() {
            return new ArrayList<>(tasks);
        }
    }

    //görev listesini arka planda yedekler
    static class BackupService implements Runnable {
        private final ToDoList toDoList;

        public BackupService(ToDoList toDoList) {
            this.toDoList = toDoList;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(50000); // Her 50 saniyede bir yedekleme yapar
                    backupTasks();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Yedekleme işlemi durduruldu.");
            }
        }

        private void backupTasks() {
            System.out.println("\n=== Görevler Yedekleniyor ===");
            for (String task : toDoList.getTasks()) {
                System.out.println("Yedeklenen görev: " + task);
            }
        }
    }

    //arka planda görevlerin hatırlatmasını yapar
    static class ReminderService implements Runnable {
        private final ToDoList toDoList;

        public ReminderService(ToDoList toDoList) {
            this.toDoList = toDoList;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(55000); // Her 55 saniyede bir hatırlatma yapar
                    remindTasks();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Hatırlatma işlemi durduruldu.");
            }
        }

        private void remindTasks() {
            System.out.println("\n=== Görev Hatırlatma ===");
            for (String task : toDoList.getTasks()) {
                System.out.println("Hatırlatma: " + task);
            }
        }
    }

    public static void main(String[] args) {
        ToDoList toDoList = new ToDoList(); 
        Scanner scanner = new Scanner(System.in);

        System.out.println("To-Do List Uygulamasına Hoş Geldiniz!");

        // Yedekleme ve hatırlatma işlemleri için iş parçacıklarını başlat
        Thread backupThread = new Thread(new BackupService(toDoList));
        backupThread.setDaemon(true);  // Daemon thread olarak ayarlanır, ana program kapanınca otomatik kapanır
        backupThread.start();

        Thread reminderThread = new Thread(new ReminderService(toDoList));
        reminderThread.setDaemon(true);
        reminderThread.start();

        boolean exit = false;
        while (!exit) {
            showMenu();
            System.out.print("Bir seçenek girin: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  

            switch (choice) {
                case 1 -> toDoList.addTask();
                case 2 -> toDoList.listTasks();
                case 3 -> toDoList.deleteTask();
                case 4 -> exit = true;
                default -> System.out.println("Geçersiz seçenek! Tekrar deneyin.");
            }
        }

        System.out.println("Uygulamadan çıkılıyor...");
    }

  
    private static void showMenu() {
        System.out.println("\n==== Menü ====");
        System.out.println("1 - Görev Ekle");
        System.out.println("2 - Görevleri Listele");
        System.out.println("3 - Görev Sil");
        System.out.println("4 - Çıkış");
    }
}
