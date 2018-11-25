package ps.gov.epsilon.hr.firm.recruitment

import ps.gov.epsilon.hr.firm.lookups.CommitteeRole
import ps.gov.epsilon.hr.firm.lookups.Inspection
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.lookups.InspectionCategoryService


class ApplicantInspectionResultTagLib {

    InspectionCategoryService inspectionCategoryService
    ApplicantInspectionCategoryResultService applicantInspectionCategoryResultService

    def renderInspection = { attrs, body ->
        ApplicantInspectionCategoryResult applicantInspectionCategoryResult
        InspectionCategory inspectionCategory

        //if there is no params
        if (!attrs.inspectionCategoryId && !attrs.applicantInspectionCategoryResultId) {
            out << ""
        } else {

            //check if there is an applicant inspection category result for applicant
            if (attrs.applicantInspectionCategoryResultId) {
                //to get applicant inspection category result
                params["encodedId"] = attrs.applicantInspectionCategoryResultId
                applicantInspectionCategoryResult = applicantInspectionCategoryResultService.getInstance(params)
                //assign inspection category settings  id
                params["id"] = applicantInspectionCategoryResult?.inspectionCategory?.id
                params.remove("encodedId")
            }

            //to get inspection category settings
            if (attrs.inspectionCategoryId) {
                params["id"] = attrs.inspectionCategoryId
            }

            inspectionCategory = inspectionCategoryService.getInstance(params)
            //if there is an inspection category
            if (inspectionCategory) {

                //to render inspection category
                out << lay.wall([title: "${message(code: 'inspectionCategory.label')} : " + inspectionCategory?.descriptionInfo?.localName, color: "purple"]) {
                }
                out << el.hiddenField(name: 'inspectionCategory', value: inspectionCategory?.id)


                out << el.formGroup([:]) {
                    out << el.dateField([name: "requestDate", value: applicantInspectionCategoryResult?.requestDate, size: 4, class: " isRequired", label: "${message(code: 'applicantInspectionCategoryResult.requestDate.label', default: 'requestDate')}"])
                    out << el.dateField([name: "receiveDate", value: applicantInspectionCategoryResult?.receiveDate, size: 4, class: " ", label: "${message(code: 'applicantInspectionCategoryResult.receiveDate.label', default: 'receiveDate')}"])

                }
                out << el.formGroup([:]) {

                    out << el.select(valueMessagePrefix: 'EnumInspectionResult', value: applicantInspectionCategoryResult?.inspectionResult, size: 4, name: "inspectionResult", from: ps.gov.epsilon.hr.enums.v1.EnumInspectionResult.values() - ps.gov.epsilon.hr.enums.v1.EnumInspectionResult.NEW, class: "isRequired", label: "${message(code: 'applicantInspectionCategoryResult.inspectionResult.label', default: 'inspectionResult')}")

                    if (inspectionCategory?.hasResultRate) {
                        out << el.select(valueMessagePrefix: 'EnumInspectionResultRate', value: applicantInspectionCategoryResult?.inspectionResultRate, size: 4, name: "inspectionResultRate", from: ps.gov.epsilon.hr.enums.v1.EnumInspectionResultRate.values(), class: " ", label: "${message(code: 'applicantInspectionCategoryResult.inspectionResultRate.label', default: 'inspectionResultRate')}")

                    }
                    if (inspectionCategory?.hasMark) {
                        out << el.textField( value: applicantInspectionCategoryResult?.mark, size: 4, name: "mark",  class: "", label: "${message(code: 'applicantInspectionCategoryResult.mark.label', default: 'mark')}")

                    }
                }

                out << el.formGroup([:]){
                    out << el.textArea(name: "resultSummary", value: applicantInspectionCategoryResult?.resultSummary, size: 4, class: " ", label: "${message(code: 'applicantInspectionCategoryResult.resultSummary.label', default: 'resultSummary')}")

                }

                // check if there is a committee for inspection category
                if (inspectionCategory?.committeeRoles) {

                    out << "   <div style=\"padding-right: 40px;,padding-bottom: 15px;\">\n" +
                            "            <h4 class=\" smaller lighter blue\">\n" +
                            "                    ${message(code: 'applicantInspectionCategoryResult.info.label')}</h4> <hr/></div>"

                    // to get list of committee role for inspection category
                    List<JoinedInspectionCategoryResultCommitteeRole> joinedInspectionCategoryResultCommitteeRoleList = applicantInspectionCategoryResult?.committeeRoles?.toList()
                    List<CommitteeRole> inspectionCategoryCommitteeRoleList = inspectionCategory?.committeeRoles?.committeeRole?.sort {
                        it?.id
                    }

                    inspectionCategoryCommitteeRoleList?.each { CommitteeRole committeeRole ->
                        //to render committee role for inspection category
                        out << el.formGroup([:]) {
                            out << el.hiddenField(name: "committeeRole", value: committeeRole?.id)
                            out << el.textField(name: "partyName", value: joinedInspectionCategoryResultCommitteeRoleList?.find {
                                it?.committeeRole?.id == committeeRole?.id
                            }?.partyName, size: 4, class: " ", label: committeeRole?.descriptionInfo?.localName)
                        }

                    }
                }
            }
            out << el.row([:])


            ApplicantInspectionResult applicantInspectionResult
            //get sorted inspections list  that related to the selected inspection category
            List<Inspection> inspectionList = inspectionCategory?.inspections?.sort {
                it.orderId
            }
            //start rendering the inspection information
            inspectionList?.each { Inspection inspection ->
                //find the inspection previous entered information
                applicantInspectionResult = applicantInspectionCategoryResult?.testsResult?.find {
                    it?.inspection?.id == inspection?.id
                }

                //render inspection and inspection results
//                out << lay.wall([title: "${message(code: 'inspection.label')} : " + inspection?.descriptionInfo?.localName])
                out << el.hiddenField(name: 'inspectionIds', value: inspection?.id)

                out << "   <div style=\"padding-right: 40px;,padding-bottom: 15px;\">\n" +
                        "            <h4 class=\" smaller lighter blue\">\n" +
                        "                    ${message(code: 'inspection.label')} :  ${inspection?.descriptionInfo?.localName}</h4> <hr/></div>"


                //to render send and receive date if inspection has date
                if (inspection?.hasDates) {
                    out << el.formGroup([:]) {
                        out << el.dateField([name: "inspectionSendDate", size: 4, value: applicantInspectionResult?.sendDate, class: " ", label: "${message(code: 'applicantInspectionResult.sendDate.label', default: 'sendDate')}"])
                        out << el.dateField([name: "inspectionReceiveDate", size: 4, value: applicantInspectionResult?.receiveDate, class: " ", label: "${message(code: 'applicantInspectionResult.receiveDate.label', default: 'receiveDate')}"])
                    }
                }

                out << el.formGroup([:]) {
                    out << el.textField(name: "inspectionResultValue", size: 4, value: applicantInspectionResult?.resultValue, class: " ", label: "${message(code: 'applicantInspectionResult.resultValue.label', default: 'resultValue')}")
                    if (inspection?.hasPeriod) {
                        out << el.textField(name: "inspectionExecutionPeriod", size: 4, value: applicantInspectionResult?.executionPeriod, class: " ", label: "${message(code: 'applicantInspectionResult.executionPeriod.label', default: 'executionPeriod')}")
                    } else {
                        out << el.hiddenField(name: "inspectionExecutionPeriod", size: 4, value: applicantInspectionResult?.executionPeriod, class: " ", label: "${message(code: 'applicantInspectionResult.executionPeriod.label', default: 'executionPeriod')}")

                    }
                }
                out << el.formGroup([:]) {
                    out << el.textArea(name: "inspectionResultSummary", size: 4, value: applicantInspectionResult?.resultSummary, class: " ", label: "${message(code: 'applicantInspectionResult.resultSummary.label', default: 'resultSummary')}")
                    if (inspection?.hasMark) {
                        out << el.textField(name: "inspectionMark", size: 4, value: applicantInspectionResult?.mark, class: " ", label: "${message(code: 'applicantInspectionResult.mark.label', default: 'mark')}")
                    } else {
                        out << el.hiddenField(name: "inspectionMark", size: 4, value: applicantInspectionResult?.mark, class: " ", label: "${message(code: 'applicantInspectionResult.mark.label', default: 'mark')}")

                    }
                }

                //to get list of committee role for inspection
                List<JoinedInspectionResultCommitteeRole> inspectionResultCommitteeRole = applicantInspectionResult?.committeeRoles?.toList()
                List<CommitteeRole> inspectionCommitteeRoleList = inspection?.committeeRoles?.committeeRole?.sort {
                    it?.id
                }

                //to render inspection committee role
                inspectionCommitteeRoleList?.each { CommitteeRole committeeRole ->

                    //to render committee role for inspection
                    out << el.formGroup([:]) {
                        out << el.hiddenField(name: inspection?.id + "_committeeRole", value: committeeRole?.id)
                        out << el.textField(name: inspection?.id + "_partyName", value: inspectionResultCommitteeRole?.find {
                            it?.committeeRole?.id == committeeRole?.id
                        }?.partyName, size: 4, class: " ", label: committeeRole?.descriptionInfo?.localName)
                    }
                }
            }

            out << el.row([:])

        }
    }
}
