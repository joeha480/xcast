<?xml version="1.0" encoding="utf-8"?>
<!--
		*** Check pagenum ***
		(DESC)
		Joel Håkansson, TPB
		Version 2005-06-02
  -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<html>
			<head/>
			<body>
				<p>Första sidan: <xsl:value-of select="descendant::pagenum[1]"/></p>
				<p><xsl:apply-templates select="descendant::pagenum[1]"/></p>
				<p>Sista sidan: <xsl:value-of select="descendant::pagenum[last()]"/></p>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="pagenum">
		<xsl:param name="previous" select="0"/>
		<xsl:variable name="current">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:variable name="diff">
			<xsl:value-of select="$current -$previous -1"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$diff&gt;1">Sidnummer saknas: <xsl:value-of select="concat($previous+1,'-',$current -1)"/></xsl:when>
			<xsl:when test="$diff=1">Sidnummer saknas: <xsl:value-of select="$previous+1"/></xsl:when>
			<xsl:when test="$diff&lt;0">Sidnummer i oordning: <xsl:value-of select="$current"/>
			</xsl:when>
		</xsl:choose>
		<xsl:if test="$diff!=0"><br/></xsl:if>
		<xsl:apply-templates select="following::pagenum[1]">
			<xsl:with-param name="previous" select="$current"/>
		</xsl:apply-templates>
	</xsl:template>
	
</xsl:stylesheet>
