class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
		
		//"/getReports" (controller: 'getReports', action: 'loadTestExistingReports')

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
