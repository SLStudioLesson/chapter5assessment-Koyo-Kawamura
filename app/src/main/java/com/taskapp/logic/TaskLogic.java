package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;

    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * 
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        // リスト作成
        List<Task> tasks = taskDataAccess.findAll();

        // 取得したデータを表示する
        tasks.forEach(task -> {
            String status;
            switch (task.getStatus()) {

                case 1:
                    status = "着手中";
                    break;
                case 2:
                    status = "完了";
                    break;
                default:
                    status = "未着手";
                    break;
            }

            User assignedUser = task.getRepUser();
            String repUserName;

            // 担当者情報が取得できているか確認
            if (assignedUser == null) {
                System.out.println("担当者情報がnullです。タスクコード: " + task.getCode());
            }

            if (assignedUser != null) {
                if (assignedUser.getCode() == loginUser.getCode()) {
                    repUserName = "あなたが担当しています"; // ログインユーザーが担当者の場合
                } else {
                    repUserName = assignedUser.getName() + "が担当しています"; // 担当者の名前を表示
                }
            } else {
                repUserName = "担当者情報が見つかりません"; // 担当者が見つからなかった場合
            }

            // 出力
            System.out.println(task.getCode() + ". " + "タスク名：" + task.getName() +
                    ", 担当者名：" + repUserName + ", ステータス： " + status);
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code        タスクコード
     * @param name        タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser   ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int status, User loginUser) throws AppException {

        User repUser = userDataAccess.findByCode(loginUser.getCode());
        if (repUser == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }

        Task newTask = new Task(code, name, 0, repUser); // 初期ステータスは0（未着手）

        taskDataAccess.save(newTask);
        System.out.println("タスクの登録が完了しました。");

        // ログを保存
        Log newLog = new Log(code, 0, loginUser.getCode(), LocalDate.now()); // 初期ステータスと日付を含む
        logDataAccess.save(newLog);

    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code      タスクコード
     * @param status    新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status, User loginUser) throws AppException {

        if (!isNumeric(codeInput)) {
            System.out.println("半角の整数で入力してください。");
            System.out.println();
            continue;
        }
        // 選択肢一覧表示
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}
