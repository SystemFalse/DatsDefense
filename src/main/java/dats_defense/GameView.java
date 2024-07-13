package dats_defense;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameView extends JFrame {
    public static final int TIME_ZONE = 3 * 60 * 60 * 1000;
    public static final Date REGISTRATION_TIME;

    static {
        try {
            REGISTRATION_TIME = DateFormat.getTimeInstance().parse("0:05:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private JLabel roundTime;
    private boolean initialized = false;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private int maxBaseSize;

    public GameView() throws HeadlessException {
        super("Game view");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setAlwaysOnTop(true);
        init();
    }

    public void init() {
        if (initialized) {
            return;
        }
        roundTime = new JLabel();
        roundTime.setMaximumSize(new Dimension(400, 20));
        add(roundTime);
        JLabel defeated = new JLabel();
        add(defeated);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel stats = new JPanel(new GridLayout(9, 2));
        JLabel round = new JLabel("Раунд:");
        stats.add(round);
        JLabel roundName = new JLabel();
        stats.add(roundName);
        JLabel currentRound = new JLabel("Текущий ход:");
        stats.add(currentRound);
        JLabel currentRoundValue = new JLabel();
        stats.add(currentRoundValue);
        JLabel gold = new JLabel("Золото:");
        stats.add(gold);
        JLabel goldValue = new JLabel();
        stats.add(goldValue);
        JLabel points = new JLabel("Очки:");
        stats.add(points);
        JLabel pointsValue = new JLabel();
        stats.add(pointsValue);
        JLabel enemyBlockKills = new JLabel("Убито блоков врага:");
        stats.add(enemyBlockKills);
        JLabel enemyBlockKillsValue = new JLabel();
        stats.add(enemyBlockKillsValue);
        JLabel zombieKills = new JLabel("Убито зомби:");
        stats.add(zombieKills);
        JLabel zombieKillsValue = new JLabel();
        stats.add(zombieKillsValue);
        JLabel headHealth = new JLabel("Здоровье Центра управления:");
        stats.add(headHealth);
        JLabel headHealthValue = new JLabel();
        stats.add(headHealthValue);
        JLabel baseSize = new JLabel("Размер базы:");
        stats.add(baseSize);
        JLabel baseSizeValue = new JLabel();
        stats.add(baseSizeValue);
        JLabel maxBaseSizeLabel = new JLabel("Макс. размер базы:");
        stats.add(maxBaseSizeLabel);
        JLabel maxBaseSizeValue = new JLabel();
        stats.add(maxBaseSizeValue);
        add(stats);
        executor.submit(() -> {
            while (true) {
                ZombiedefResponse response;
                try {
                    response = DatsDefense.getZombiedef();
                } catch (IOException e) {
                    continue;
                }
                Round r = response.rounds.stream().dropWhile((rn -> !rn.status.equals("active"))).findFirst().orElse(null);
                if (r == null) {
                    continue;
                }
                Date now = response.now;
                Date startAt = r.startAt;
                Date endAt = r.endAt;
                if (now.after(startAt) && now.before(endAt)) {
                    Date timeRemaining = new Date(endAt.getTime() - now.getTime() - TIME_ZONE);
                    Date timeFromStart = new Date(now.getTime() - startAt.getTime() - TIME_ZONE);
                    String text = "Время с начала раунда: " +
                            DateFormat.getTimeInstance().format(timeFromStart) +
                            ", осталось: " +
                            DateFormat.getTimeInstance().format(timeRemaining);
                    this.roundTime.setText(text);
                    if (!Game.IN_GAME && timeFromStart.before(REGISTRATION_TIME)) {
                        DatsDefense.putParticipate();
                    }
                    if (!Game.IN_GAME && timeFromStart.after(REGISTRATION_TIME)) {
                        executor.submit(() -> {
                            try {
                                maxBaseSize = 0;
                                Game.main();
                            } catch (IOException | InterruptedException e) {
                                System.err.println(e.getMessage());
                            }
                        });
                        Thread.sleep(500);
                    }
                    if (Game.IN_GAME) {
                        roundName.setText(Game.WORLD.realmName);
                        currentRoundValue.setText(String.valueOf(Game.UNITS.turn + 1));
                        goldValue.setText(String.valueOf(Game.UNITS.player.gold));
                        pointsValue.setText(String.valueOf(Game.UNITS.player.points));
                        enemyBlockKillsValue.setText(String.valueOf(Game.UNITS.player.enemyBlockKills));
                        zombieKillsValue.setText(String.valueOf(Game.UNITS.player.zombieKills));
                        for (Base base : Game.UNITS.base) {
                            if (base.isHead) {
                                headHealthValue.setText(String.valueOf(base.health));
                                break;
                            }
                        }
                        baseSizeValue.setText(String.valueOf(Game.UNITS.base.size()));
                        if (Game.UNITS.base.size() > maxBaseSize) {
                            maxBaseSize = Game.UNITS.base.size();
                            maxBaseSizeValue.setText(String.valueOf(maxBaseSize));
                        }
                    } else if (Game.UNITS != null && Game.UNITS.base.isEmpty()) {
                        defeated.setText("Мы проиграли в " + DateFormat.getTimeInstance().format(Game.UNITS.player.gameEndedAt));
                    }
                }
                Thread.sleep(2000);
            }
        });
        initialized = true;
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        GameView view = new GameView();
        view.setVisible(true);
    }
}
