package com.taskapp.dataaccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.taskapp.exception.AppException;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskDataAccess {

    private final String filePath;
    private final UserDataAccess userDataAccess;

    public TaskDataAccess() {
        filePath = "app/src/main/resources/tasks.csv";
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param filePath
     * @param userDataAccess
     */
    public TaskDataAccess(String filePath, UserDataAccess userDataAccess) {
        this.filePath = filePath;
        this.userDataAccess = userDataAccess;
    }

    /**
     * CSVから全てのタスクデータを取得します。
     *
     * @return タスクのリスト
     */
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // ヘッダー行を読み飛ばす
            
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                
                // CSVに間違いがあったらスキップする
                if (values.length != 4) {
                    continue;
                }
                
                // int code, String name, int status, User repUser
                int code = Integer.parseInt(values[0]);
                String name = values[1];
                int status = Integer.parseInt(values[2]);
                User repUser = userDataAccess.findByCode(Integer.parseInt(values[3]));
                
                // Taskオブジェクトを作成しリストに追加
                Task task = new Task(code, name, status, repUser);
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    /**
     * タスクをCSVに保存します。
     * 
     * @param task 保存するタスク
     */
    public void save(Task task) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = createLine(task);
            writer.newLine();
            writer.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * コードを基にタスクデータを1件取得します。
     * 
     * @param code 取得するタスクのコード
     * @return 取得したタスク
     */
    public Task findByCode(int code) throws AppException {


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                int taskCode = Integer.parseInt(fields[0]);

                if (taskCode == code) {
                    // Taskのコンストラクタに応じて適切に引数を指定
                    return new Task(taskCode, fields[1], Integer.parseInt(fields[2]), userDataAccess.findByCode(Integer.parseInt(fields[3])));
                }
            }
        } catch (IOException e) {
            throw new AppException("タスクの検索中にエラーが発生しました" + e.getMessage());
        }
        return null; // タスクが見つからなかった場合
    }

    /**
     * タスクデータを更新します。
     * 
     * @param updateTask 更新するタスク
     */
    public void update(Task updateTask) throws AppException {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                int taskCode = Integer.parseInt(fields[0]);

                // 更新対象のタスクを見つけた場合、更新する
                if (taskCode == updateTask.getCode()) {
                    tasks.add(updateTask); // 新しいタスク情報を追加
                } else {
                    // それ以外は元のデータを保持
                    User user = userDataAccess.findByCode(Integer.parseInt(fields[3]));
                    tasks.add(new Task(taskCode, fields[1], Integer.parseInt(fields[2]), user));
                }
            }
        } catch (IOException e) {
            throw new AppException("タスクの更新中にエラーが発生しました" + e.getMessage());
        }

        // 更新後のタスクリストをCSVに書き込む
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task t : tasks) {
                bw.write(createLine(t));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new AppException("書き込み中にエラーが発生 " + e.getMessage());
        }
    }

    /**
     * コードを基にタスクデータを削除します。
     * 
     * @param code 削除するタスクのコード
     */
    // public void delete(int code) {
    //     try () {
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    /**
     * タスクデータをCSVに書き込むためのフォーマットを作成します。
     * 
     * @param task フォーマットを作成するタスク
     * @return CSVに書き込むためのフォーマット文字列
     */
    private String createLine(Task task) {
        return task.getCode() + "," + task.getName() + "," + task.getStatus() + "," + task.getRepUser().getCode();
    }
}
