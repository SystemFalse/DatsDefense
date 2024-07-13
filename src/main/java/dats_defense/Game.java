package dats_defense;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static dats_defense.DatsDefense.*;

public class Game {
    public static WorldResponse WORLD;
    public static UnitsResponse UNITS;
    public static boolean IN_GAME;

    private static final ExecutorService service = Executors.newSingleThreadExecutor();

    public static void main(String... args) throws IOException, InterruptedException {
        final List<Point> walls = new ArrayList<>();

        IN_GAME = true;
        do {
            service.submit(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    WORLD = getWorld();
                    UNITS = getUnits();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                if (UNITS.base.isEmpty()) {
                    IN_GAME = false;
                    return;
                }
                try {
                    // Обработка состояния игры и принятие решений
                    processGameState(WORLD, UNITS, walls);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.printf("%d turn took %dms%n", UNITS.turn + 1, elapsedTime);
            });
            Thread.sleep(2000);
        } while (IN_GAME);
    }

    private static List<Zombie> getZombiesInRange(Base baseCell, List<Zombie> zombies) {
        int attackRange = baseCell.isHead() ? 8 : 5;
        return zombies.parallelStream()
                .filter(zombie -> calculateDistance(baseCell.getX(), baseCell.getY(), zombie.x, zombie.y) <= attackRange)
                .collect(Collectors.toList());
    }

    private static List<EnemyBlock> getEnemyBaseCellsInRange(Base baseCell, List<EnemyBlock> enemyBaseCells) {
        int attackRange = baseCell.isHead() ? 8 : 5;
        return enemyBaseCells.parallelStream()
                .filter(cell -> calculateDistance(baseCell.getX(), baseCell.getY(), cell.getX(), cell.getY()) <= attackRange)
                .collect(Collectors.toList());
    }

    private static boolean isValidPosition(int x, int y, List<Base> baseCells, List<Point> walls, Base headCell, List<Zombie> zombies, List<EnemyBlock> enemyBaseCells) {
        // Проверка на отсутствие других клеток базы
        for (Base cell : baseCells) {
            if (cell.getX() == x && cell.getY() == y) {
                return false;
            }
        }
        // Проверка на отсутствие стен
        for (Point wall : walls) {
            if (wall.getX() == x && wall.getY() == y) {
                return false;
            }
        }
        // Проверка на отсутствие зомби
        for (Zombie zombie : zombies) {
            if (zombie.getX() == x && zombie.getY() == y) {
                return false;
            }
        }
        // Проверка на отсутствие клеток базы других игроков
        for (EnemyBlock enemyCell : enemyBaseCells) {
            if (enemyCell.getX() == x && enemyCell.getY() == y) {
                return false;
            }
            // Проверка на радиус в 1 клетку от клеток базы других игроков
            if (Math.abs(enemyCell.getX() - x) <= 1 && Math.abs(enemyCell.getY() - y) <= 1) {
                return false;
            }
        }
        // Проверка на радиус в 1 клетку от собственных клеток базы
        for (Base cell : baseCells) {
            if (Math.abs(cell.getX() - x) <= 1 && Math.abs(cell.getY() - y) <= 1) {
                return true;
            }
        }
        return false;
    }

    private static List<int[]> getAvailablePositionsForBuilding(List<Base> baseCells, List<Point> walls, Base headCell, List<Zombie> zombies, List<EnemyBlock> enemyBaseCells) {
        List<int[]> availablePositions = new ArrayList<>();
        // Реализация логики поиска доступных для строительства позиций вокруг существующих клеток базы
        for (Base cell : baseCells) {
            // Пример логики добавления доступных позиций вокруг клетки базы
            addIfValid(availablePositions, cell.getX() + 1, cell.getY(), baseCells, walls, headCell, zombies, enemyBaseCells);
            addIfValid(availablePositions, cell.getX() - 1, cell.getY(), baseCells, walls, headCell, zombies, enemyBaseCells);
            addIfValid(availablePositions, cell.getX(), cell.getY() + 1, baseCells, walls, headCell, zombies, enemyBaseCells);
            addIfValid(availablePositions, cell.getX(), cell.getY() - 1, baseCells, walls, headCell, zombies, enemyBaseCells);
        }
        return availablePositions;
    }

    private static void addIfValid(List<int[]> positions, int x, int y, List<Base> baseCells, List<Point> walls, Base headCell, List<Zombie> zombies, List<EnemyBlock> enemyBaseCells) {
        if (isValidPosition(x, y, baseCells, walls, headCell, zombies, enemyBaseCells)) {
            positions.add(new int[]{x, y});
        }
    }

    private static void processGameState(WorldResponse worldResponse, UnitsResponse unitsResponse, List<Point> walls) throws IOException {
        // Получение списка всех зомби
        List<Zombie> zombies = unitsResponse.getZombies();
        List<Base> baseCells = unitsResponse.getBase();
        List<EnemyBlock> enemyBaseCells = unitsResponse.getEnemyBlocks();

        // Получение информации о центре управления
        Base headCell = baseCells.stream().filter(Base::isHead).findFirst().orElse(null);
        if (headCell == null) {
            return; // В случае если центр управления не найден, прерываем обработку
        }

        // Принятие решений по атаке
        CommandRequest commandRequest = new CommandRequest();
        TreeMap<Point, AttackTarget> attackTargets = new TreeMap<>();

        for (Base baseCell : baseCells) {
            List<Zombie> zombiesInRange = getZombiesInRange(baseCell, zombies);
            List<EnemyBlock> enemyBaseCellsInRange = getEnemyBaseCellsInRange(baseCell, enemyBaseCells);
            for (Zombie zombie : zombiesInRange) {
                AttackTarget target = new AttackTarget(zombie, baseCell);
                attackTargets.merge(new Point(zombie.x, zombie.y), target, (t1, t2) -> {
                    t1.attackingBases.addAll(t2.attackingBases);
                    return t1;
                });
            }
            for (EnemyBlock enemyBlock : enemyBaseCellsInRange) {
                AttackTarget target = new AttackTarget(enemyBlock, baseCell);
                attackTargets.merge(new Point(enemyBlock.x, enemyBlock.y), target, (t1, t2) -> {
                    t1.attackingBases.addAll(t2.attackingBases);
                    return t1;
                });
            }
        }
        HashSet<Base> usedBases = new HashSet<>();
        for (var target : attackTargets.entrySet().stream().sorted(Comparator.comparing(e -> e.getValue().target.ordinal())).toList()) {
            AttackTarget at = target.getValue();
            at.attackingBases.removeAll(usedBases);
            int health = at.health;
            while (health > 0 && !at.attackingBases.isEmpty()) {
                var itr = at.attackingBases.iterator();
                Base attackingBase = itr.next();
                itr.remove();
                usedBases.add(attackingBase);
                health -= attackingBase.attack;
                commandRequest.getAttack().add(new Attack(attackingBase.id, target.getKey()));
            }
            if (usedBases.size() == UNITS.base.size()) {
                break;
            }
        }

        // Принятие решений по переносу базы
        TreeMap<Point, MoveTarget> moveTargets = new TreeMap<>();
        for (Base base : baseCells) {
            moveTargets.put(new Point(base.x, base.y), new MoveTarget(getEnemyBaseCellsInRange(base, enemyBaseCells).size()));
        }
        for (Zombie zombie : unitsResponse.getZombies()) {
            if (zombie.type == Zombie.ZombieType.normal) {
                for (Base base : baseCells) {
                    if (base.x == zombie.x && base.y == zombie.y - 1 && zombie.direction == Zombie.Direction.down) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.x == zombie.x && base.y == zombie.y + 1 && zombie.direction == Zombie.Direction.up) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x == zombie.x + 1 && zombie.direction == Zombie.Direction.right) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x == zombie.x - 1 && zombie.direction == Zombie.Direction.left) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    }
                }
            }
            if (zombie.type == Zombie.ZombieType.fast) {
                for (Base base : baseCells) {
                    if (base.x == zombie.x && base.y - zombie.y < 2 && zombie.direction == Zombie.Direction.down) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.x == zombie.x && base.y - zombie.y > -2 && zombie.direction == Zombie.Direction.up) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x - zombie.x < 2 && zombie.direction == Zombie.Direction.right) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x - zombie.x > -2 && zombie.direction == Zombie.Direction.left) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    }
                }
            }
            if (zombie.type == Zombie.ZombieType.bomber) {
                for (Base base : baseCells) {
                    if (base.x == zombie.x && base.y == zombie.y - 1 && zombie.direction == Zombie.Direction.down) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                Point p = new Point(base.x + dx, base.y + dy);
                                if (moveTargets.containsKey(p)) {
                                    moveTargets.get(new Point(base.x + dx, base.y + dy)).attackingZombies++;
                                }
                            }
                        }
                    } else if (base.x == zombie.x && base.y == zombie.y + 1 && zombie.direction == Zombie.Direction.up) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                Point p = new Point(base.x + dx, base.y + dy);
                                if (moveTargets.containsKey(p)) {
                                    moveTargets.get(new Point(base.x + dx, base.y + dy)).attackingZombies++;
                                }
                            }
                        }
                    } else if (base.y == zombie.y && base.x == zombie.x + 1 && zombie.direction == Zombie.Direction.right) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                Point p = new Point(base.x + dx, base.y + dy);
                                if (moveTargets.containsKey(p)) {
                                    moveTargets.get(new Point(base.x + dx, base.y + dy)).attackingZombies++;
                                }
                            }
                        }
                    } else if (base.y == zombie.y && base.x == zombie.x - 1 && zombie.direction == Zombie.Direction.left) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                Point p = new Point(base.x + dx, base.y + dy);
                                if (moveTargets.containsKey(p)) {
                                    moveTargets.get(new Point(base.x + dx, base.y + dy)).attackingZombies++;
                                }
                            }
                        }
                    }
                }
            }
            if (zombie.type == Zombie.ZombieType.liner) {
                for (Base base : baseCells) {
                    if (base.x == zombie.x && base.y < zombie.y && zombie.direction == Zombie.Direction.down) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.x == zombie.x && base.y > zombie.y && zombie.direction == Zombie.Direction.up) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x < zombie.x && zombie.direction == Zombie.Direction.right) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x > zombie.x && zombie.direction == Zombie.Direction.left) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    }
                }
            }
            if (zombie.type == Zombie.ZombieType.juggernaut) {
                for (Base base : baseCells) {
                    if (base.x == zombie.x && base.y == zombie.y - 1 && zombie.direction == Zombie.Direction.down) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.x == zombie.x && base.y == zombie.y + 1 && zombie.direction == Zombie.Direction.up) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x == zombie.x + 1 && zombie.direction == Zombie.Direction.right) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    } else if (base.y == zombie.y && base.x == zombie.x - 1 && zombie.direction == Zombie.Direction.left) {
                        moveTargets.get(new Point(base.x, base.y)).attackingZombies++;
                    }
                }
            }
        }
        moveTargets.entrySet().stream().min(Map.Entry.comparingByValue()).ifPresent(move -> commandRequest.moveBase = move.getKey());

        // Принятие решений по строительству новых клеток базы
        List<int[]> availablePositions = getAvailablePositionsForBuilding(baseCells, walls, headCell, zombies, enemyBaseCells);
        int gold = unitsResponse.player.gold;
        for (int[] pos : availablePositions) {
            if (gold > 0) {
                commandRequest.getBuild().add(new Point(pos[0], pos[1]));
                gold--;
            } else {
                break;
            }
        }

        // Отправка команд на сервер
        CommandResponse commandResponse = postCommand(commandRequest);
        commandRequest.getBuild().removeAll(commandResponse.getAcceptedCommands().getBuild());
        walls.addAll(commandResponse.getAcceptedCommands().getBuild());
    }

    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
