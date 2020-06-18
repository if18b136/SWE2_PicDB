package main.Database;

/**
 * Factory Helper class to create Database Mocks for UnitTests
 */
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
