package ps.gov.epsilon.hr.common

import grails.converters.JSON
import guiplugin.FormatService

class SystemReportController {

    SystemReportService systemReportService
    FormatService formatService

    def index() {
        redirect(action:"list")
    }

    def list = {

    }

    def employeeRank = {
    }

    def employeeRankFilter = {
        List dataList = systemReportService.getMilitaryRankData(params)
        render text: (formatService.resultListToMap([resultList:dataList,totalCount:dataList?.size()], params,systemReportService.DOMAIN_COLUMNS) as JSON), contentType: "application/json"
    }

    def renderReport = {
        Map map = systemReportService.getGeneralReportSettings()
        map.each { key, value ->
            params["_" + key] = value
        }
        String reportType = params.remove("reportType")
        redirect(controller: "report", action: reportType, params: params)
    }

}
