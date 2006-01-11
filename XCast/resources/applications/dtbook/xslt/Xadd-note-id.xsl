<?xml version="1.0" encoding="UTF-8"?>
<!--<html><p><b>Add-note-id:</b></p>
		  <p>Lägger till <i>idref</i> på <b>noteref</b> och <i>id</i><br> 
			på <b>note</b>.</p>
		  <p>Noterna numreras löpande per <b>level1</b>.</p>
		  <p>OBS! Om <i>id</i> eller <i>idref</i> redan finns, <br>
			ändras inte dess värde.</p>
	</html>
-->
<!--
		Joel Håkansson, TPB
		Version 2005-09-13
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="no" encoding="UTF-8"/>

	<xsl:template match="/">
	    <xsl:comment>Add-note-id, version 2005-07-12 07:53</xsl:comment>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="noteref">
		<xsl:choose>
			<xsl:when test="@idref!=''"><xsl:call-template name="copy"/></xsl:when>
			<xsl:otherwise>
				<xsl:variable name="this" select="generate-id(ancestor::level1)"/>
				<xsl:element name="{name()}">
					<xsl:copy-of select="@*"/>
					<xsl:attribute name="idref">
						<xsl:value-of select="concat('fn_', count(preceding::level1), '_', count(preceding::noteref[generate-id(ancestor::level1)=$this])+1)"/>
					</xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="note">
		<xsl:choose>
			<xsl:when test="@id!=''"><xsl:call-template name="copy"/></xsl:when>
			<xsl:otherwise>
				<xsl:variable name="this" select="generate-id(ancestor::level1[1])"/>
				<xsl:element name="{name()}">
					<xsl:copy-of select="@*"/>
					<xsl:attribute name="id">
						<xsl:value-of select="concat('fn_', count(preceding::level1), '_', count(preceding-sibling::note)+1)"/>
					</xsl:attribute>
					<xsl:apply-templates/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
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
