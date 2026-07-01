#!/usr/bin/env python
# -*- coding: utf-8 -*-

# Copyright (C) 2026 University of Dundee & Open Microscopy Environment.
# All rights reserved.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

"""Tests querying of Annotations with json api."""

from omeroweb.testlib import IWebTest, get_json, delete_json
from django.urls import reverse
from omeroweb.api import api_settings
import pytest
from test_api_projects import get_update_service, \
    get_connection
from test_api_images import assert_objects
from omero.model import ProjectI, \
    DatasetI, \
    RoiI
from omero.gateway import BlitzGateway, DatasetWrapper, ProjectWrapper, CommentAnnotationWrapper, TagAnnotationWrapper

from omero.model.enums import UnitsLength
from omero.rtypes import rstring, rint, rdouble
from omero import ValidationException


def build_url(client, url_name, url_kwargs):
    """Build an absolute url using client response url."""
    response = client.request()
    # http://testserver/webclient/
    webclient_url = response.url
    url = reverse(url_name, kwargs=url_kwargs)
    url = webclient_url.replace('/webclient/', url)
    return url


class TestAnnotations(IWebTest):
    """Tests querying Annotations."""

    ns = "test_get_objects_annotation_comment"
    ns_tag = "test_get_objects_annotation_tag"

    @pytest.fixture()
    def user1(self):
        """Return a new user in a read-annotate group."""
        group = self.new_group(perms='rwra--')
        user = self.new_client_and_user(group=group)
        return user

    @pytest.fixture()
    def dataset_anns(self, user1):
        """Create a bunch of Annotations."""
        conn = get_connection(user1)
        update = get_update_service(user1)

        def create_dataset_with_annotations(name, dtype="Dataset"):
            if dtype == "Dataset":
                obj = DatasetI()
                obj.name = rstring(name)
                obj = update.saveAndReturnObject(obj)
                wrapper = DatasetWrapper(conn, obj)
            elif dtype == "Project":
                obj = ProjectI()
                obj.name = rstring(name)
                obj = update.saveAndReturnObject(obj)
                wrapper = ProjectWrapper(conn, obj)

            # create Comment
            ann = CommentAnnotationWrapper(conn)
            ann.setNs(self.ns)
            ann.setValue("Test Comment: " + name)
            ann = wrapper.linkAnnotation(ann)
            # create Tag
            tag = TagAnnotationWrapper(conn)
            tag.setNs(self.ns_tag)
            tag.setValue("Test Tag: " + name)
            wrapper.linkAnnotation(tag)

            return wrapper

        dataset1 = create_dataset_with_annotations("Dataset 1")
        dataset2 = create_dataset_with_annotations("Dataset 2")
        project1 = create_dataset_with_annotations("Project 1", dtype="Project")
        return dataset1, dataset2, project1


    def test_dataset_anns(self, user1, dataset_anns):
        """Test querying Annotations on Datasets and Projects."""
        dataset1, dataset2, project1 = dataset_anns
        conn = get_connection(user1)
        user_name = conn.getUser().getName()
        client = self.new_django_client(user_name, user_name)
        version = api_settings.API_VERSIONS[-1]

        # List ALL annotations
        annotations_url = reverse('api_annotations', kwargs={'api_version': version})
        rsp = get_json(client, annotations_url)
        # assert_objects(conn, rsp['data'], [], dtype="Roi",
        #                opts={'load_shapes': True})
        assert len(rsp['data']) == 6

        rsp = get_json(client, annotations_url, {'dataset': dataset1.id})
        # assert_objects(conn, rsp['data'], [], dtype="Roi",
        #                opts={'load_shapes': True})
        assert len(rsp['data']) == 2
