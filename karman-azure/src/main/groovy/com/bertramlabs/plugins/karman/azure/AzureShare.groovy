package com.bertramlabs.plugins.karman.azure

import com.bertramlabs.plugins.karman.CloudFile
import com.bertramlabs.plugins.karman.Directory
import groovy.json.JsonSlurper
import groovy.util.logging.Commons
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpHead
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicHeader
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.apache.http.util.EntityUtils

/**
 * Created by bwhiton on 12/02/2016.
 */
@Commons
class AzureShare extends AzureDirectory {
	
	@Override
	protected String getType() {
		return 'share'
	}

	/**
	 * List share files and directories
	 * @param options (prefix, marker, delimiter and maxKeys)
	 * @return List
	 */
	List listFiles(options = [:]) {
		AzureFileStorageProvider azureProvider = (AzureFileStorageProvider) provider

		// If 'prefix' is specified, then the request is relative to this directory
		// Construct the directory and return its listFiles
		if(options.prefix) {
			return processPrefixListFiles(options)
		} 

		def opts = [
			verb: 'GET',
			queryParams: [restype:'directory', comp: 'list'],
			path: name,
			uri: "${azureProvider.getEndpointUrl()}/${name}".toString()
		]

		def (HttpClient client, HttpGet request) = azureProvider.prepareRequest(opts) 
		HttpResponse response = client.execute(request)
		HttpEntity responseEntity = response.getEntity()
		def xmlDoc = new XmlSlurper().parse(responseEntity.content)
		EntityUtils.consume(response.entity)	

		def items = []
		if(response.statusLine.statusCode == 200) {
			xmlDoc.Entries?.File?.each { file ->
				items << new AzureFile(name: file.Name, provider: provider, shareName: this.shareName)
			}
			xmlDoc.Entries?.Directory?.each { directory ->
				items << new AzureDirectory(name: directory.Name, provider: provider, shareName: this.shareName)
			}
		} else {
			def errMessage = "Error getting items from directory with name ${name}: ${xmlDoc.Message}"
			log.error errMessage
		}

		return items
	}

	@Override
	protected String getFullPath() {
		return name
	}

	@Override 
	protected String getShareName() {
		return name
	}
}
