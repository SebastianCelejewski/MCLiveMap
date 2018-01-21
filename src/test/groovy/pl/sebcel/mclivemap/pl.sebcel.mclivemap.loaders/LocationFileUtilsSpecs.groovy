package pl.sebcel.mclivemap.loaders

import spock.lang.*

class LocationFileUtilsSpecification extends Specification {

	def "Extracting player name from location file name with full path"() {
		expect:
		LocationFileUtils.getPlayerName("/d/elemele_dutki/location-playerName-2018-01.csv", "2018-01") == "playerName"		                                          
	}

	def "Extracting player name from location file name without full path"() {
		expect:
		LocationFileUtils.getPlayerName("location-playerName-2018-01.csv", "2018-01") == "playerName"		                                          
	}
	
	def "Exctracting player name from location file with full path - version 2"() {
	
		given: "player name and date pattern"
		def actualPlayerName = "playerName"
		def datePattern = "2018-01"
		
		and: "location file name with full path"
		def locationFilePath = "/d/folder/folder/location-" + actualPlayerName + "-" + datePattern + ".csv"
		
		when: "getting player name"
		def extractedPlayerName = LocationFileUtils.getPlayerName(locationFilePath, datePattern)
		
		then: "player name should be extracted"
		extractedPlayerName == actualPlayerName
	}
	
	def "Looking for valid location files"() {
		expect:
		LocationFileUtils.matchLocationFile(locationFile, datePattern) == result
		
		where:
		locationFile                      | datePattern || result
		"location-playerName.csv"         | "2018-01"   || false  // no player name
		"location-playerName-2018-02.csv" | "2018-01"   || false  // wrong date
		"playerName-2018-01.csv"          | "2018-01"   || false  // no "locatio-" prefix
	}
}