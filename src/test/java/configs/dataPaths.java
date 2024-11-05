package configs;

public interface dataPaths {


        String dataBasePath = System.getProperty("user.dir");


        //Data Paths
        String URLs = dataBasePath+"\\TestData\\URLs.xls";
        String dataPath = dataBasePath+"\\TestData\\DataBook.xls";
        String B2CBookingE2ETestData = dataBasePath+"\\TestData\\B2CBookingFlowEndToEnd.xls";
        String contactUsDataPath = dataBasePath+"\\TestData\\ContactUsData.xls";
        String screenshotFolder = dataBasePath+"\\TestScreenShots";
        String excelOutputPath = dataBasePath+"\\TestResult\\BookingOutput.xlsx";
        String bookingReferencesForCancellation = dataBasePath+"\\TestData\\BookingReferences.xls";
        String TSPlusDataPath = dataBasePath+"\\TestData\\TS+ Subscription Data.xls";
        String deepLinks = dataBasePath+"\\TestData\\Deeplinks.xls";
        String seatsTest = dataBasePath+"\\TestData\\SeatsTest.xls";
        String b2bTestData = dataBasePath+"\\TestData\\B2BBookingData.xls";





}
