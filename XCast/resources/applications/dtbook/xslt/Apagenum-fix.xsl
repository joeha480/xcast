<?xml version="1.0" encoding="UTF-8"?>
<!--<html>
		<p><b>Pagenum-fix:</b></p>
		<p>Tar bort tomma <b>p</b> samt <b>p</b> <br>
		   runt <b>p</b> eller <b>pagenum</b>
		</p>
       <p>Tar bort <b>li</b> runt <b>pagenum</b>
	</html>-->
<!--
		Joel Håkansson, TPB
		Version 2006-06-19
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>
	
	<xsl:template match="/">
	    <xsl:comment>Pagenum fix, version 2006-06-19 17:14</xsl:comment>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="p[count(text())=0 and 
						((count(descendant::pagenum)=count(descendant::*)) or 
						 (count(descendant::p)=      count(descendant::*)))]">
		<xsl:apply-templates/>
	</xsl:template>

<!-- Nytt i version 060619 -->
	<xsl:template match="li[count(text())=0 and 
						((count(descendant::pagenum)=count(descendant::*)))]">
		<xsl:apply-templates/>
	</xsl:template>
<!-- / Nytt i version 060619 -->
	
	<xsl:template match="*">
		<xsl:call-template name="copy"/>
	</xsl:template>
	
	<xsl:template name="copy">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
