package ps.gov.epsilon.hr.firm.lookups

import grails.util.Holders
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.enums.profile.v1.EnumMilitaryRankDegree
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.domains.constrains.DescriptionInfoUniqueConstrains
import ps.police.common.domains.v1.TrackingInfo
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.HashHelper
import ps.police.config.v1.Constants

import java.time.ZonedDateTime

/**
 * <h1>Purpose</h1>
 * To hold the  military rank of an employee
 * <h1>Usage</h1>
 * Used  as to represents the military rank of an employee
 * <h1>Example</h1>
 * Major,Colonel ... etc.
 * **/

class MilitaryRank {

    String id

    String encodedId

    DescriptionInfo descriptionInfo

    //the number of years between the specified military rank and the one before it
    Short numberOfYearToPromote

    //the next military rank that an employee will be granted after the specified period like "Lieutenant then first Lieutenant"
    MilitaryRank nextMilitaryRank

    //to classify the degree(classification) of military rank
    EnumMilitaryRankDegree militaryRankDegree = EnumMilitaryRankDegree.GRADE_OFFICER

    /**
     * this code used to represent the Iso or Zenar value of the lookup entry
     * **/
    String universalCode

    Short orderNo

    TrackingInfo trackingInfo

    static belongsTo = [firm: Firm]

    static embedded = ['trackingInfo', 'descriptionInfo']

    static constraints = {
        importFrom DescriptionInfoUniqueConstrains
//        orderNo Constants.POSITIVE_SHORT + [unique: 'firm']
        orderNo Constants.POSITIVE_SHORT + [validator: { val, obj,errors ->
            MilitaryRank.withNewSession {
                def criteria = MilitaryRank.createCriteria()
                def numberOfRecords=criteria.get{
                    projections {
                        count('id')
                    }

                    eq('orderNo', obj?.orderNo)
                    eq('firm.id',(obj?.firm?.id)?:1L)
                    //to exclude the deleted records
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                    //exclude the update process
                    if(obj?.hasProperty("id") && obj?.id)
                    {
                        ne('id', obj?.id)
                    }
//                    or{
//                        eq('orderNo', val?.orderNo)
//                        eq('nextMilitaryRank', val?.nextMilitaryRank)
//                    }
                }

                if(numberOfRecords>0){
                    errors.reject('militaryRank.orderNo.error.unique')
                }
            }
            return true
        }]
        numberOfYearToPromote(Constants.POSITIVE_SHORT_NULLABLE)
        nextMilitaryRank nullable: true, widget: "autocomplete"  ,validator: { val, obj,errors ->
            MilitaryRank.withNewSession {
                def criteria = MilitaryRank.createCriteria()
                def numberOfRecords=criteria.get{
                    projections {
                        count('id')
                    }

                    eq('nextMilitaryRank', obj?.nextMilitaryRank)
                    eq('firm.id',(obj?.firm?.id)?:1L)
                    //to exclude the deleted records
                    ne("trackingInfo.status", GeneralStatus.DELETED)
                    //exclude the update process
                    if(obj?.hasProperty("id") && obj?.id)
                    {
                        ne('id', obj?.id)
                    }
                }

                if(numberOfRecords>0){
                    errors.reject('militaryRank.nextMilitaryRank.error.unique')
                }
            }
            return true
        }
        universalCode (Constants.LOOKUP_NAME_NULLABLE)
        trackingInfo nullable: true, display: false
    }

    transient springSecurityService

    static transients = ['springSecurityService', 'encodedId']

    def beforeInsert() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName
        if (!applicationName) applicationName = "BootStrap"
        trackingInfo = new TrackingInfo()
        if (!trackingInfo.createdBy)
            trackingInfo.createdBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.lastUpdatedBy)
            trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        if (!trackingInfo.sourceApplication)
            trackingInfo.sourceApplication = applicationName
        if (!trackingInfo.dateCreatedUTC)
            trackingInfo.dateCreatedUTC = ZonedDateTime.now()
        if (!trackingInfo.lastUpdatedUTC)
            trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
        if (!trackingInfo.ipAddress)
            trackingInfo.ipAddress = "localhost"
    }

    def beforeUpdate() {
        def applicationName = Holders.grailsApplication.config?.grails?.applicationName;
        if (!applicationName) applicationName = "BootStrap";
        trackingInfo.lastUpdatedBy = springSecurityService?.principal?.username ?: applicationName
        trackingInfo.lastUpdatedUTC = ZonedDateTime.now()
    }

    public String getEncodedId() {
        return HashHelper.encode(id.toString())
    }


    @Override
    public String toString() {
        return descriptionInfo.toString();
    }
}
