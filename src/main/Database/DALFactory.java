package main.Database;

public class DALFactory {
    private static boolean useMock = false;

    public static void useMock() {
        useMock = true;
    }

    public static DAL getDAL() {
        if(useMock) {
            return new DALMock();
        } else {
            return DataAccessLayer.getInstance();
        }
    }
}
